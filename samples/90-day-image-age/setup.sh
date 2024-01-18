#!/bin/sh
# Create namespace for Old Image
oc new-project old-image --display-name 'Old Image Registry'

# Deploy standalone docker registry
oc new-app registry -e REGISTRY_STORAGE_DELETE_ENABLED=true -n old-image
oc create route edge registry --service=registry -n old-image

# Copy image from quay.io into registry
REGISTRY_HOST=$(oc get route registry --template='{{ .spec.host }}' -n old-image)
skopeo copy docker://quay.io/aromero/dc-metro-map:latest docker://$REGISTRY_HOST/aromero/dc-metro-map:latest

# Create a new app referencing the temporary image
oc new-app $REGISTRY_HOST/aromero/dc-metro-map:latest -n old-image
oc patch deployment dc-metro-map -p '{"spec":{"template":{"spec":{"containers":[{"name":"dc-metro-map","imagePullPolicy":"Always"}]}}}}'-n old-image
oc expose service/dc-metro-map -n old-image
