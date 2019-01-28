# Angular 7, .NET Core Web API with EF core,MSSQL server, VsCode, Azure DevOps CI/CD, Azure Container Service, Google Kubernetes Engine, Terraform

This is a sample project showing the steps taken to develop a simple 3 tier application using Angular 7, .NET Core Web API with EF core and MSSQL server using open Open Source development tools (VsCode) using a Windows 10 Professional laptop. Azure DevOps is used to automate the CI/CD process and deploy to both Azure Kubernetes Service and Goolge Kubernetes Engine using Terraform

## Step 1: Install Docker for Windows
1. Make sure you have Windows 10 Pro or Enterprise (Docker for Windows require Hyper-V)
2. Install Docker for Windows at [https://docs.docker.com/docker-for-windows/install/](https://docs.docker.com/docker-for-windows/install/)
3. Make sure Docker is running in Linux mode

## Step 2: Create Angular 7 image
1. install latest angular-cli using `npm i -g @angular/cli`
2. create new folder called `Docker` somewhere on your computer
3. inside `Docker` folder create new Angular App by using `ng new angular-ui` command
5. inside the `angular-ui` folder, add new file `nginx.conf` and `Dockerfile`
6. create new docker image by running `docker build --rm -f "angular-ui\Dockerfile" -t nvhoanganh1909/docker-demo-ui:latest angular-ui` command from the root folder. **Note**:
   1. `nvhoanganh1909` is your Docker Hub username.
   2. `docker-demo-ui` is the name of the image
   3. `:latest` is the tag or the version number of the image. 
7. check to make sure image is created by running `docker images` command
8.   run the newly created image by running `docker run -d --rm -p 4200:80 nvhoanganh1909/docker-demo-ui:latest` where
9.  open http://localhost:4200 , you should see the Angular app is running

**Note**:
- view the running docker container by running `docker ps`
- stop the running container by running `docker container stop 0f` where `0f` is the first 2 characters of the Container ID 
- run `docker system prune --volumes -f` to remove all unused images and containers

## Step 3: Create Microsoft SQL Server Container Image
1. login to [https://hub.docker.com/](https://hub.docker.com/)
2. search for `microsoft SQL linux`
3. select [https://hub.docker.com/r/microsoft/mssql-server-linux/](https://hub.docker.com/r/microsoft/mssql-server-linux/)
4. check instructions on how to use this image
5. now pull down the latest version of this image by running `docker pull microsoft/mssql-server-linux:latest`
6. start the container using SQLExpress mode by running `docker run -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=Password1' -e 'MSSQL_PID=Express' -p 1433:1433 -d microsoft/mssql-server-linux:latest` (change `SA_PASSWORD` to something else)
7. you now can connect to this SQL server using SQL Management Studio

## Step 4: Create new .NET Core WebApi
1. from the root folder (`Docker`), create new folder called `dotnetcore-api` using `mkdir dotnetcore-webapi`
2. cd into the `dotnetcore-webapi` and run `dotnet new webapi` 
3. edit `dotnetcore-webapi.csproj` and add `<DockerDefaultTargetOS>Linux</DockerDefaultTargetOS>` element under `<TargetFramework>netcoreapp2.1</TargetFramework>` element.
6. in the dotnetcore-webapi folder create `Dockerfile` and `.dockerignore` file
8. Build the docker image by running `docker build --rm -f "dotnetcore-webapi\Dockerfile" -t nvhoanganh1909/docker-demo-user-api:latest dotnetcore-webapi` from the root folder
9.  now run the webapi by running `docker run -d -p 8080:80 nvhoanganh1909/docker-demo-user-api` 
10. open http://localhost:8080/api/values and you should be able to see the response from the API

## Step 5: Create new .NET Core Api Gateway using Ocelot
1. from the root folder (`Docker`), create new folder called `api-gateway`
2. cd into the `api-gateway` and run `dotnet new webapi` 
3. add `ocelot` nuget package
4. modify `Startup.cs` and `Program.cs` accordingly

**NOTE**:
- Ocelot uses information stored in `configuration.json` to route requests to the correct API. During development the configuration.json file under `api-gateway\configuration` 
- When we deploy the app, we will use Volume mapping to replace this file with the correct config file

## Step 6: Wiring up connections between containers
In this step, we will add a simple User management functionality to our system using ASP .NET Identity. 
1. Add ASP .NET Identity using SQL [See Instructions](https://docs.microsoft.com/en-us/aspnet/core/security/authentication/identity?view=aspnetcore-2.2&tabs=visual-studio)
2. Add Data Seeder to create 1 user called **admin**
3. Create `UsersController.cs` which expose CRUD REST endpoints at http://localhost/api/user
5. Modify Angular UI which call the api gateway and return the list of users (calling http://localhost/api/u/users)
6. Now if you run all 3 apps at the same time (Web API + Ocelot API Gateway + Angular) you should be the app running

## Step 7: Run the whole app using Docker-compose file
1. go to the root level (`Docker` folder) and create new file called `docker-compose.yml`. This file describe how containers are started and stopped
2. run `docker-compose up` to run the app
3. run `docker-compose down` to shut down the app

## Step 8: Pushing images to Docker Hub
1. Login to docker hub by running `docker login -u nvhoanganh1909`
2. Tag the local image `docker tag f16 nvhoanganh1909/docker-demo-ui:latest` where `f16` is the first 3 characters of your imagek
3. Push the newly tagged image `docker push nvhoanganh1909/docker-demo-ui:latest`
4. Login to https://hub.docker.com/ and make sure you can see the new docker image
   
## Step 9: Add Kubernetes
1. From Docker for Windows Settings, turn on Kubernetes option
2. Verify Kubenetes is running by running `kubectl cluster-info` command
3. Create new K8s namespace ([k8s-namespace](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/)) by running `kubectl create namespace dev`
4. Register user context by running `kubectl config set-context dev --namespace=dev --cluster=docker-for-desktop-cluster --user=docker-for-desktop`
5. Switch to dev context `kubectl config use-context dev`
6. Create ConfigMap `$ kubectl.exe create configmap webbffconfigmap --from-file=./src/api-gateway/web.bff/config/configuration.json`
7.  Install `nginx ingress controller` by running `kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/mandatory.yaml` then run `kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/provider/cloud-generic.yaml`
8. Create resources defined in the `k8s.yml` in `dev` context by running `kubectl apply -f k8s.yml --record` 
9. Run `kubectl get all` ande make sure all services and pods are running
10. Browse http://localhost/ and make sure you can see the app running
11. At anytime you can run `kubectl delete daemonsets,replicasets,services,deployments,pods,rc --all --namespace=dev` to remove all created resources in the dev namespace


**Notes**:
- get current rollout update status `kubectl.exe rollout status deployments webui-deployment`
- get Deployment history  `kubectl.exe rollout history deployments webui-deployment`
- check current deployment details `kubectl.exe describe deploy webui-deployment`
- to roll back `kubectl.exe rollout undo deployment webui-deployment --to-revision=1`
- run Bash command inside a pod `kubectl.exe exec apigw-pod -i -t -- bash`

## Step 10: Manually deploy to Azure AKS

### Part 1. set up AKS Cluster
1. create new AKS via Azure Portal
1. download and install Azure CLI 
1. switch context to AKS `az aks get-credentials --resource-group dockertest --name dockerdemo` where `dockertest` is the resource group name and `dockerdemo` is the name of your AKS cluster
1. install [Helm](https://docs.helm.sh/using_helm/#quickstart-guide) and add to your PATH
2. make sure current context for kubectl is your Azure AKS by running `kubectl.exe config current-context` and `kubectl.exe config use-context dockerdemo` to switch to the correct context
3. run (in Git Bash)
```bash
helm init
kubectl create serviceaccount --namespace kube-system tiller
kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
kubectl patch deploy --namespace kube-system tiller-deploy -p '{"spec":{"template":{"spec":{"serviceAccount":"tiller"}}}}'
```
### Part 2: Install Istio
1. install Istio using Helm https://istio.io/docs/setup/kubernetes/helm-install/
   
### Part 2: Create Staging and Prod namespace
1. run `kubectl.exe create ns staging` and `kubectl.exe create ns prod` 
2. create new staging and prod context `kubectl config set-context dockerdemo-staging --namespace=staging --cluster=dockerdemo --user=clusterUser_dockertest_dockerdemo` and `kubectl config set-context dockerdemo-prod --namespace=prod --cluster=dockerdemo --user=clusterUser_dockertest_dockerdemo`

### Part 3: Create the app on staging and prod
1. change context to dockerdemo-staging `kubectl config use-context dockerdemo-staging`
2. Create ConfigMap `$ kubectl.exe create configmap webbffconfigmap --from-file=./src/api-gateway/web.bff/config/configuration.json`
3. run `kubectl.exe apply -f k8s.yml -f k8s.staging.yml --record`
4. wait for deployment to be completed and navigate to http://staging.52.187.236.54.xip.io/. You should see the app is now running in Azure AKS
5. Repate the step above for prod

**Note**:
- try to delete the Prod or Staging deployment by running `kubectl.exe delete -f k8s.yml`
- re-run `kubectl.exe apply -f k8s.yml -f k8s.prod.yml --record` 

## Step 11: Make changes to Angular UI app
Let's make a small change to the app
- switch context to dev `kubectl.exe config use-context dev`
- make sure our app is running at http://localhost (if not ,just run `kubectl.exe apply -f k8s.yml --record`)
- run `ng s` inside `angular-ui` folder
- now the angular app running at http://locahost:4200 will use the api running in the local Dev cluster (http://localhost/api)
- make the change you want to the angular app
- build new image `docker build --rm -f "angular-ui\Dockerfile" -t nvhoanganh1909/docker-demo-ui:4 angular-ui` where `4` is the new versio number
- update k8s.yml file with the new image number `image: nvhoanganh1909/docker-demo-ui:4`
- run `kubectl.exe apply -f k8s.yml --record`
- make sure new version is running at http://localhost
- push new image to Docker hub `docker push nvhoanganh1909/docker-demo-ui:4` 
- switch context to `kubectl.exe config use-context dockerdemo-staging`
- deploy new chages to Staging by running `kubectl.exe apply -f k8s.yml --record`
- now go to http://staging.52.187.236.54.xip.io/ and you should see the new version is running
- check http://prod.52.187.236.54.xip.io/ and you should see the old version is still running
  
## Step 12: Manually Deploy to Google Kubernetes Engine
Now we will deploy the same app above to Google Cloud 

### Part 1. set up GKE Cluster with static IP using HELM
- Create new Google Cloud app at https://console.cloud.google.com/projectcreate , call it dockerdemo 
- Create new K8s Cluster https://console.cloud.google.com/kubernetes/add?project=dockerdemo-227313&template=your-first-cluster where dockerdemo-227313 is the newly created project Id
- download `gcloud.exe` from https://cloud.google.com/sdk/docs/#install_the_latest_cloud_tools_version_cloudsdk_current_version
- run `gcloud container clusters get-credentials your-first-cluster-1 --zone asia-east1-a --project dockerdemo-227313`
- view list of contexts `kubectl.exe config view`
- switch to the correct context `kubectl.exe config use-context gke_dockerdemo-227313_asia-east1-a_your-first-cluster-1`
- create static Ip `gcloud compute addresses create dockerdemo-ip --global` (note, ip name must be lower case)
- get the ip address `gcloud compute addresses describe dockerdemo-ip --global`
- run the following 3 commands

```bash
helm init
kubectl create serviceaccount --namespace kube-system tiller
kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
kubectl patch deploy --namespace kube-system tiller-deploy -p '{"spec":{"template":{"spec":{"serviceAccount":"tiller"}}}}'
```
 
### Part 2: Create Staging and Prod namespace
1. run `kubectl.exe create ns staging` and `kubectl.exe create ns prod` 
2. create new staging and prod context `kubectl config set-context gke-dockerdemo-staging --namespace=staging --cluster=gke_dockerdemo-227313_asia-east1-a_your-first-cluster-1 --user=gke_dockerdemo-227313_asia-east1-a_your-first-cluster-1` and `kubectl config set-context gke-dockerdemo-prod --namespace=prod --cluster=gke_dockerdemo-227313_asia-east1-a_your-first-cluster-1 --user=gke_dockerdemo-227313_asia-east1-a_your-first-cluster-1`

### Part 3: Create the app on staging and prod
1. change context to gke-dockerdemo-staging `kubectl config use-context gke-dockerdemo-staging`
2. Create ConfigMap `$ kubectl.exe create configmap webbffconfigmap --from-file=./src/api-gateway/web.bff/config/configuration.json`
3. run `kubectl.exe apply -f k8s.yml -f k8s.staging.gke.yml --record`
4. wait for deployment to be completed and navigate to http://staging.35.241.14.155.xip.io/. You should see the app is now running in Azure AKS
5. Repate the step above for prod

## Step 13: Automate CI/CD using Azure DevOps

## Step 12: Deploy to Azure Container Service and Google Kubernetes Engine using Terraform

https://www.hanselman.com/blog/HowToSetUpKubernetesOnWindows10WithDockerForWindowsAndRunASPNETCore.aspx

