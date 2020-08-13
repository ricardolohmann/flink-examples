## Description

This project is used to run a Word Count example in using Flink Native
Kubernetes feature.

What happens after running ?

When we apply the Kubernetes file, a pod named `cluster-job-submit` will start
and run a command to start a job cluster in Kubernetes.
After that, a Job Manager and a Flink job will start but, the Flink job will
stay in `Created` status waiting for resources.
Then, 2 Task Managers will be started and the job will start running.
Flink will clean up all the pods which were started for running Job Managers
and Task Managers after the job finishes.

## Building Project

1. Build the jar file
    ```shell
    ./gradlew clean shadowJar
    ```
2. Build the docker image
    ```shell
    eval $(minikube docker-env)
    docker build -t wordcount:latest .
    ```

## Running

1. Start MiniKube
    ```shell
    minikube start --cpus=4 --memory=4096
    minikube ssh 'sudo ip link set docker0 promisc on'
    ```
2. Setup permissions
    ```shell
    kubectl create serviceaccount flink
    kubectl create clusterrolebinding flink-role-binding-flink --clusterrole=edit --serviceaccount=default:flink
    ```
3. Run native Flink Kubernetes
    ```shell
    kubectl apply -k .kube/
    ```
4. Verify running pods
    ```shell
    kubectl get pods
    ```