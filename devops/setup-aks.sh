# create new Azure resource group
az.cmd group create --name rbus-asia --location SoutheastAsia
az.cmd aks create -g rbus-asia -n rbus --location SoutheastAsia --kubernetes-version 1.10.9 --enable-addons http_application_routing --generate-ssh-keys
az.cmd aks get-credentials --resource-group rbus-asia --name rbus

#  install Helm
choco install kubernetes-helm

# init helm on the Azure cluster
helm init

kubectl create serviceaccount --namespace kube-system tiller
kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
kubectl patch deploy --namespace kube-system tiller-deploy -p '{"spec":{"template":{"spec":{"serviceAccount":"tiller"}}}}'

# Create 2 namespaces
kubectl create ns staging
kubectl create ns prod

# create 2 more context to map to each namespace
kubectl config set-context rbus-staging --namespace=staging --cluster=rbus --user=clusterUser_rbus-asia_rbus
kubectl config set-context rbus-prod --namespace=prod --cluster=rbus --user=clusterUser_rbus-asia_rbus

# change context to staging
kubectl config use-context rbus-staging

# download istio from https://github.com/istio/istio/releases and add istioctl into your PATH
# install istio into your k8s cluster using helm 
# from the location where you download istio from 
helm install install/kubernetes/helm/istio --name istio --namespace istio-system
# enable grafana and jaeger 
helm upgrade --recreate-pods --namespace istio-system --set kiali.enabled=true --set grafana.enabled=true --set tracing.enabled=true --set global.configValidation=false --set sidecarInjectorWebhook.enabled=false istio install/kubernetes/helm/istio
# instal https://preliminary.istio.io/docs/tasks/telemetry/kiali/
helm template --set kiali.enabled=true install/kubernetes/helm/istio --name istio --namespace istio-system > $HOME/istio.yaml
helm template --set kiali.enabled=true --set "kiali.dashboard.jaegerURL=http://$(kubectl get svc tracing --namespace istio-system -o jsonpath='{.spec.clusterIP}'):80" --set "kiali.dashboard.grafanaURL=http://$(kubectl get svc grafana --namespace istio-system -o jsonpath='{.spec.clusterIP}'):3000" install/kubernetes/helm/istio --name istio --namespace istio-system > $HOME/istio.yaml
kubectl apply -f $HOME/istio.ya

# make sure istio is now installed
kubectl get svc -n istio-system
kubectl get pods -n istio-system

# make sure automatic istio side car injection is enabled for staging and prod https://istio.io/docs/setup/kubernetes/sidecar-injection/#automatic-sidecar-injection
kubectl label namespace staging istio-injection=enabled
kubectl label namespace prod istio-injection=enabled

# cd into the root of the Git repo
cd /c/AnthonyNguyenData/source/personal/rbus-docker

# create configMap
kubectl.exe create configmap webbffconfigmap --from-file=./src/api-gateway/web.bff/config/configuration.json

# now deploy the app
kubectl.exe apply -f config/k8s/ -f config/k8s/Azure/ -f config/istio --record

# get list of pods and make sure there are 2 containers running in each POD (one of them is the istio-proxy)
kubectl.exe get pods

# get details of a pod
kubectl.exe describe pod authentication-service-deploy-5c7874f96d-g6xj4

# get logs from the worker container (your code)
kubectl.exe logs authentication-service-deploy-5c7874f96d-g6xj4 -c worker

# get public IP of Istio ingress
kubectl.exe get svc -n istio-system



# run Grafana and http://localhost:3000/dashboard/db/istio-mesh-dashboard
kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=grafana -o jsonpath='{.items[0].metadata.name}') 3000:3000 &

# open http://localhost:3000/dashboard/db/istio-mesh-dashboard
# run jaeger
kubectl port-forward -n istio-system $(kubectl get pod -n istio-system -l app=jaeger -o jsonpath='{.items[0].metadata.name}') 16686:16686 &

# run kiali
kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=kiali -o jsonpath='{.items[0].metadata.name}') 20001:20001

# open http://localhost:20001 and login with admin:admin