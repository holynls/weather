# DelightRoom Backend Take Home Assignment

## Usage
### Requirements
> openJDK 17

### Docker Local Image Build
``` shell
./gradlew interfaces:jibDockerBuild
```

### Run
```shell
docker run -d -p 8080:8080 weather-api
```
