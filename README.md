# Telnet Server with SSL - Spring Boot

## Overview
This is a **secure Telnet Server** built using **Spring Boot** and **Apache MINA**. 
It supports **multiple client connections** with **SSL encryption**, ensuring secure communication over Telnet.

## Features
✔️ Secure Telnet communication  
✔️ SSL/TLS encryption using Java Keystore  
✔️ Handles multiple client connections  
✔️ Built with Spring Boot and Apache MINA  

## Setup Instructions
### 1️⃣ Clone the Repository
```sh
git clone https://github.com/your-github-username/your-repo-name.git
cd your-repo-name

```2️⃣ Configure SSL (Keystore)```  
```🚨 Important: Since the Keystore file (keystore.jks) is not included in the repository for security reasons,```   
``` you need to generate or provide your own.```  

``` **Generate a Keystore (If Not Available)** ```  

keytool -genkey -alias telnetserver -keyalg RSA -keystore keystore.jks -storepass changeit -validity 365

``` 3️⃣ Run the Application```  
```mvn spring-boot:run```  

```4️⃣ Connect to the Server```    
telnet localhost 8081
