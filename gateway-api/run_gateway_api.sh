#!/bin/bash

printf "\n\n==================== Preparando projeto Gateway-API ====================\n\n"

if ! command -v mvn &> /dev/null; then
    echo "Maven não está instalado no sistema. Por favor, instale o Maven e tente novamente."
    exit 1
fi

if [ ! -f "pom.xml" ]; then
    echo "O arquivo pom.xml não foi encontrado no diretório gateway-api. Certifique-se de que esteja no diretório correto."
    exit 1
fi

mvn clean package

docker build -t gateway-api-1.0.0 .

if [ $? -eq 0 ]; then
    echo "Construção do projeto concluída com sucesso!"
else
    echo "Ocorreu um erro durante a construção do projeto."
fi

NOME_CONTAINER="gateway-api"

if docker ps -a --filter "name=${NOME_CONTAINER}" --format '{{.Names}}' | grep -q "${NOME_CONTAINER}"; then
  echo "Parando e removendo o contêiner ${NOME_CONTAINER} existente..."
  docker stop ${NOME_CONTAINER}
  docker rm ${NOME_CONTAINER}
fi

echo "Criando e executando um novo contêiner ${NOME_CONTAINER}..."
docker compose up -d

echo "Contêiner ${NOME_CONTAINER} criado e em execução!"
