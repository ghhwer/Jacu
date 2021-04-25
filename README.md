# Jacu
<img src="https://github.com/ghhwer/Jacu/blob/main/Jacu.png?raw=true" alt="Jacu Logo" width="500"/>

Jacu is a REST Service for running and managing multiple SSH connections using a REST API. 

This can be useful for creating internal telemetry services.


PLEASE do not expose SSH connections outside of your internal network (unless you realy know what your doing). This project was not designed for production enviroments (yet... who knows...) There's still a lot of work to be done.

Here is how it goes, this is an ordinary spring boot application, compile it, debug it and run it as such. (https://spring.io/blog/2014/03/07/deploying-spring-boot-applications)

The endpoins are very similar to that of Apache Livy (In fact, the project was inspired by it) [https://livy.incubator.apache.org/]

Endpoints (I apologise for the crude documentation):
<img src="https://raw.githubusercontent.com/ghhwer/Jacu/main/JacuEndpoints.png" alt="endpoints" />
