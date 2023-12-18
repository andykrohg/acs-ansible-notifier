# 90-Day Image Age Sample
In this example, we kick ACS into overdrive by empowering it to exact swift revenge on any workloads that are running images older than 90 days. Blowing past registering an informational error, this sample will scale down the offending workload and destroy the image in the source registry, making it impossible for the workload to continue running until a new build is performed.

1. Run `setup.sh` to deploy a Docker registry and sample app (`skopeo` must be installed)
2. Use the `main.yml` playbook to initiate vengence