# Apache Flink Examples

The main idea of this project is to provide isolated examples of how to use some of the main features of Apache Flink and its tools.

## Requirements

Make sure you have the following requirements.

### Minikube

> Minikube is a tool that makes it easy to run Kubernetes locally.

All the examples run in 
[Minikube](https://kubernetes.io/docs/setup/learning-environment/minikube/).

Follow the **[guide](https://kubernetes.io/docs/tasks/tools/install-minikube/)** and install it.

After that, run the following commands to start it.

```shell
minikube start --cpus=4 --memory=4096
minikube ssh 'sudo ip link set docker0 promisc on'
```

### Kustomize

Kustomize provides a solution for customizing Kubernetes resource configuration. Some features are already built in Kubernetes latest versions. We use some that are not, so, we need to install it.

Follow one of the
**[installation instructions](https://kubernetes-sigs.github.io/kustomize/installation/)**
or run the following command:

```shell
curl -s "https://raw.githubusercontent.com/kubernetes-sigs/kustomize/master/hack/install_kustomize.sh" | bash
```

## Minikube Dashboard

At any moment you can execute `minikube dashboard` to  use Dashboard to get an
overview of applications running on your cluster.
