# Publisher and Subscriber Coordinates
### :bar_chart: A data processing application in real time, using the concept of messaging :postbox:

<br/>

## :page_facing_up: About
[Pub/Sub Coordinates](https://github.com/arthur-david/trixlog-pub-sub-coordinates) is a project divided into three parts, aiming to enable the real-time sending and receiving of geographical coordinates. The three parts of the project are:

### Part 1: Gateway API Microservice (Spring)

In this first part, we developed a microservice using the Spring technology to function as a Gateway API. This component is responsible for receiving coordinates sent by different devices or external applications. Upon receiving, the coordinates are forwarded to a RabbitMQ messaging service, ensuring an efficient and secure flow of data.

### Part 2: RabbitMQ Messaging Service

In this stage, we created a messaging service using RabbitMQ. We used a Fanout exchange to broadcast received messages to all associated queues. This approach allows multiple instances of the event processing microservice to receive coordinates simultaneously, enabling parallel processing and ensuring high availability. The messages are queued in a Queue, awaiting processing by the processing microservice.

### Part 3: Event Processing Microservice (Spring)

In this final part, we implemented a microservice also based on Spring, which functions as the event processor for received coordinates. Using a Listener connected to the RabbitMQ Queue, this microservice receives coordinates in real-time, dequeues the messages, and initiates processing. The coordinates are subjected to a series of business rules, being validated and transformed as necessary. Once processed, the events are stored in a MongoDB database, making the data available for future queries and analysis.

The project as a whole aims to offer a scalable and robust solution for sending and receiving geographical coordinates, allowing easy integration with other applications and systems. The use of microservices and reliable technologies such as Spring and RabbitMQ ensures the performance and reliability of the system, as well as enables expansion and adaptation for future needs.

**Observação:**
Lembre-se de ajustar as configurações do RabbitMQ, MongoDB e demais detalhes do projeto de acordo com as necessidades específicas de sua aplicação.

## :rocket: Run
### :shell: With .sh files:

You can run applications without being the traditional way with a shell script that uses docker containers, see the step by step below

***:warning: Requirements***

*You must have installed:*
- *[Docker](https://docs.docker.com/get-docker/)*
- *[Docker Compose](https://docs.docker.com/compose/install/)*
- *[Maven](https://maven.apache.org/download.cgi)*

To run all applications in docker containers use the command:
``` bash
./run_apps.sh
```

To stop all applications in docker containers use the command:
``` bash
./stop_apps.sh
```

<br/>

**:heavy_exclamation_mark: Observação :**
To run applications with the traditional method, remember to adjust the RabbitMQ, MongoDB settings and other project details according to the specific needs of the applications. To change project settings, review the ```application.properties``` of each application

## 🛠 Technologies

The following tools were used in building the project:

- [Java](https://www.java.com/pt-BR/)
- [Spring](https://spring.io/)
- [Maven](https://maven.apache.org/index.html)
- [RabbitMQ](https://www.rabbitmq.com/)
- [MongoDB](https://www.mongodb.com/pt-br)
- [Docker](https://www.docker.com/)

## Author

<a href="https://github.com/arthur-david">
 <img style="border-radius: 50%;" src="https://avatars.githubusercontent.com/u/53877762?v=4" width="100px;" alt=""/>
 <br />
 <sub><b>Arthur David</b></sub></a>


Created with dedication by Arthur David Contact me 👋🏽!

[![Linkedin Badge](https://img.shields.io/badge/-Arthur-blue?style=flat-square&logo=Linkedin&logoColor=white&link=https://www.linkedin.com/in/arthur-david-bb9214142/)](https://www.linkedin.com/in/arthur-david-bb9214142/) 
[![Gmail Badge](https://img.shields.io/badge/-arthurdavid000@gmail.com-c14438?style=flat-square&logo=Gmail&logoColor=white&link=mailto:arthurdavid000@gmail.com)](mailto:arthurdavid000@gmail.com)