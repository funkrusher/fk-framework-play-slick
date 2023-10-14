
Authentication, Authentifizierung, RBAC. JWT-Token,...
wie kann dies im Play-Framework verankert werden. Beispiel und Code-Integration.


----
/signup
- clientId
- email
- password
- firstname
- lastname
- roleId

return: userSub
----
/signin
- email
- password

return: 3 Cookies (text-plain result)
- access_token
- refresh_token
- id_token
----
/protected
- expected the id_token to be valid and only allows access if yes.
- roles-check
- master-tenant-check

---------------------

da dies ein Beispielprojekt ist sollte die Simple Nutzung und Demonstrationszweck im Vordergrund stehen.
Daher keinen externen Service einbinden, sondern sehr simpel bauen damit einfach verständlich und für 
thematiken wie rollen und mandanten / user checks nutzbar.

gutes Beispiel, genutzt:
- https://pedrorijo.com/blog/scala-play-auth/