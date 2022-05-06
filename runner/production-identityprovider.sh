fuser -k 10500/tcp || true
fuser -k 8080/tcp || true
export $(cat production-identityprovider/.env | xargs -d '\n')
java -jar production-identityprovider/libs/identityprovider-0.0.1-SNAPSHOT.jar \
    --spring.application.name=identity-provider \
    --grpc.server.port=10500