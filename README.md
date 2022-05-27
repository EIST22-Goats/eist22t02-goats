# eist22t02-goats
---
## Usage

### Login to registry
create a personal access token under https://github.com/settings/tokens with permissons for `repo:status, repo_deployment, public_repo, security_events, read:packages` and login to the registry with
```bash
docker login ghcr.io
```

### Run container
open a terminal and run
```
docker run -d -p 8080:8080 --name tum_social -t ghcr.io/eist22-goats/eist22t02-goats \
&& open http://localhost:8080/
```
