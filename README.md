# README - Review Realm

Review Realm fue desarrollada con Spring MVC y Java 8. La aplicación utiliza Maven como herramienta de gestión de dependencias y construcción del proyecto. Además, se utilizan las siguientes versiones de las bibliotecas principales:

    Spring Security 4.2.0
    Hibernate 5.1.0

## Configuración

Para configurar adecuadamente la aplicación, asegúrese de seguir estos pasos:
1. Clonar el repositorio:
```bash
git clone git@bitbucket.org:itba/paw-2023a-04.git
```
2. Importar el proyecto:

3. Configuración de las claves:

La aplicación utiliza claves para el módulo "Remember Me" y para la configuración de envío de correos electrónicos. Asegúrese de ubicar los siguientes archivos en las rutas correspondientes:

    Remember Me key: Copie el archivo rememberme_key.pem en la siguiente ruta: /webapp/src/main/resources/keys/rememberme_key.pem.
    Propiedades de correo: Coloque el archivo de propiedades mailing.properties en la siguiente ruta: /webapp/src/main/resources/mailing/mailing.properties.

Asegúrese de proporcionar los valores adecuados en el archivo mailing.properties para la configuración del servidor de correo electrónico.

4. Configuración de la base de datos:

La aplicación utiliza Hibernate para la capa de persistencia. Asegúrese de configurar correctamente los detalles de la base de datos en el archivo application.properties ubicado en /webapp/src/main/resources/.

```
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
mailing.weburl=
```
5. Ejecución

Una vez que haya realizado la configuración adecuada, puede ejecutar la aplicación web siguiendo estos pasos:

* Construya el proyecto Maven:

```
mvn clean
mvn build
```

* Ejecutar la aplicacion con Tomcat Server