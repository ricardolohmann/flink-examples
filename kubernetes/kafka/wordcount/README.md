# Description

Run a Word Count example that count words from one Kafka topic and send to another.

**Features:**
* [Flink Native Kubernetes](https://ci.apache.org/projects/flink/flink-docs-stable/ops/deployment/native_kubernetes.html)
feature to deploy a job cluster. 
* Kafka Operator for Kubernetes to deploy Kafka
([Strimzi](https://github.com/strimzi/strimzi-kafka-operator))

## Building

Build the jar file

```shell
./gradlew clean shadowJar
```

Build the docker image

```shell
eval $(minikube docker-env)
docker build -t wordcount2:latest .
```

## Running

We have examples for processing the following data types:

* **[String Example](docs/processing_strings.md)**

* **[JSON Example](docs/processing_json.md)**

## FAQ

**Manual Cleanup Job Cluster**

```shell
kubectl delete -f .kube/cluster-job-submit.yaml; \
kubectl delete deployment/wordcount-cluster -n default; \
kubectl delete pods --all -n default
```

**Get Job Manager logs**

```shell
kubectl logs -f -l component=jobmanager
```

**Get Task Manager logs**

```shell
kubectl logs -f -l component=taskmanager
```
