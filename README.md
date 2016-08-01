This example illustrate sharing an actor system injecting services and exchanging messages between actors in different bundles

> gradle clean build
> gradle :system:run

This launches an "empty" OSGi framework 

Open the webconsole at http://localhost:8080/system/console (admin/admin)

Install and start system, app1 and app2 bundles (under build/libs) using the "install/update" button

