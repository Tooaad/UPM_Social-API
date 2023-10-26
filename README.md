# APIRest
API Restful from Service Oriented Systems course

## Subject
Implement a working prototype of a facebook-like social network. Users must chat with others via posts and messages. Also they must have the possibility to add or remove friends, check their profile, messages and posts. Here are some operations of the API:

- Add/Delete a user
- Check basic data of any user
- List all the users on the network. This list should filter by name
- Post/Edit/Delete a message on your profile page
- List all the messages on your profile page. This list should filter by range
- Add/Delete a friend
- List all your friends on your profile page. This list should filter by range
- Send a message to the profile page of a friend
- List all the message sent to other friends. This list should filter by date and range
- List friends messages. This list should filter by content message

## Tools 
- Java 8
- Tomcat 9
- Postman
- Maven 

## JSON URIs
* #### GET /usuarios
[![Image from Gyazo](https://i.gyazo.com/51644a82c98aa5
409136aded4bdafe53.png)](https://gyazo.com/51644a82c98aa5409136aded4bdafe53)

> Show all users registered

---
* #### GET /usuarios/Kayle
[![Image from Gyazo](https://i.gyazo.com/5e9195ff6a42a6f30662b83b9d75f2ca.png)](https://gyazo.com/5e9195ff6a42a6f30662b83b9d75f2ca)

> Show personal data of the user specified

---
* #### GET /usuarios/Kayle/pagina_personal
[![Image from Gyazo](https://i.gyazo.com/b466e3a821b10f70e51e02d086089ee3.png)](https://gyazo.com/b466e3a821b10f70e51e02d086089ee3)

> Show all posts (URIs) of the user's personal page

* #### GET /usuarios/Kayle/pagina_personal + user filter
[![Image from Gyazo](https://i.gyazo.com/4e7c168e998378a4a11a3e163693fb3e.png)](https://gyazo.com/4e7c168e998378a4a11a3e163693fb3e)

> Show all posts created by a certain user (URIs) of the user's personal page 

----
* #### GET /usuarios/Kayle/pagina_personal/1
[![Image from Gyazo](https://i.gyazo.com/473cacf325008674f3160dab551df8ca.png)](https://gyazo.com/473cacf325008674f3160dab551df8ca)

> Show post data

---
* #### POST /usuarios/Kayle/pagina_personal
[![Image from Gyazo](https://i.gyazo.com/9ee6e585dff3a1d4294181a286ffa84c.png)](https://gyazo.com/9ee6e585dff3a1d4294181a286ffa84c)

> Post a new message on Kayle's personal page

----
* #### GET /usuarios/Kayle/amigos
[![Image from Gyazo](https://i.gyazo.com/caffac2bf757551b9fdeb265c5f9c842.png)](https://gyazo.com/caffac2bf757551b9fdeb265c5f9c842)

> Show all your current friends

* #### GET /usuarios/Kayle/amigos + name filter
[![Image from Gyazo](https://i.gyazo.com/58a65dc72039015b437e3192215a7eb5.png)](https://gyazo.com/58a65dc72039015b437e3192215a7eb5)

> Same but show names that contains the content filter

----
* #### GET /usuarios/Kayle/amigos/nuevos_mensajes
[![Image from Gyazo](https://i.gyazo.com/fb5f3f85a31fc3e10667bc16dd65c937.png)](https://gyazo.com/fb5f3f85a31fc3e10667bc16dd65c937)

> Show all new friend posts on their personal page's

* #### GET /usuarios/Kayle/amigos/mensajes + content filter
[![Image from Gyazo](https://i.gyazo.com/3eee13bc73ad1f227188f8c98a73ff30.png)](https://gyazo.com/3eee13bc73ad1f227188f8c98a73ff30)

> Show all messages that contains the filter on their message content

----
* #### GET /usuarios/Kayle/perfil
[![Image from Gyazo](https://i.gyazo.com/b34ff18eca9ad55c75be9780c818c3f7.png)](https://gyazo.com/b34ff18eca9ad55c75be9780c818c3f7)

> Show public data of the user, such as friends, last messages, etc

---
#### Other
Data should have been stored on a SQL database though it was not mandatory, so it is saved on datos.txt  
