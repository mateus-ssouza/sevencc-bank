
# Sevencc Bank API 🏦

A Sevencc Bank API fornece endpoints para operações bancárias essenciais. <br>
Esta API é projetada para permitir que os clientes interajam com o sistema bancário de forma segura e eficiente. 

A API está dividida em seções que cobrem o gerenciamento de agencias, clientes, contas, transações e outros recursos relevantes.

## Descrição do Projeto

A API permite que os usuários realizem ações como:

- Autenticação do usuário.
- Gerenciamento de agências.
- Gerenciamento de clientes.
- Gerenciamento de contas.
- Transações bancárias realizadas pelos clientes.
- Geração de extrato de uma conta.
- Entre outros recursos.

## Instalação

Clone o repositório:


```bash
  git clone <URL_DO_REPOSITÓRIO>
```

```bash
  cd <NOME_DO_PROJETO>
```

Limpando e construindo o projeto:

```bash
  mvn clean install
```

## Execução

Após realizar os comandos anteriores, execute os seguintes comandos:

Executando a aplicação

```bash
  mvn spring-boot:run
```

Executando os testes

```bash
  mvn test
```

Executando os testes com jacoco

```bash
  mvn clean test jacoco:report
```

Onde o relatório dos testes ficará localizado no arquivo **index.html** na pasta **target/site/jacoco/**

## Tecnologias utilizadas

![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-FF6347?style=for-the-badge&logo=java&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-FF9900?style=for-the-badge&logo=mockito&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)

