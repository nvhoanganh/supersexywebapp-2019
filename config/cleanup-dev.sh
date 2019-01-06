kubectl delete daemonsets,replicasets,services,deployments,pods,rc,ingress --all --namespace=dev
docker system prune --volumes -f