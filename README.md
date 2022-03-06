# bootcamp-reactive-blog

#Forma de validar las reglas

## Regla 1. El usuario debe autenticarse.
Hacer un GET con la siguiente instrucción:
    http://localhost:8080/users/query?login=<usuario>&password=<password>

Se valida que encuentre al usuario, de ser así la autenticación es exitosa, caso contrario es fallida.

## Regla 2. Un autor debe tener solo un usuario.
Hacer un POST con la siguiente instrucción para crear un usuario:
    http://localhost:8080/users

Hacer un PUT con la siguiente instrucción para modificar un usuario:
    http://localhost:8080/users

Los datos del usuario deben ir en el body.

Además de validar que un autor solo tenga un usuario, se valida que el login sea único.

## Regla 3. Un autor puede tener máximo 03 blogs.
Hacer un POST con la siguiente instrucción para crear un blog:
    http://localhost:8080/blogs

Hacer un PUT con la siguiente instrucción para modificar un blog:
    http://localhost:8080/blogs

Los datos del blog deben ir en el body.

En el caso del POST, se valida que un autor no pueda tener más de 3 blogs activos
En el caso del PUT, no se permite modificar un blog de estado inactivo a activo

El blog se crea con estado activo.
Si se quiere inactivar un blog para pruebas se puede usar la opción:
    Hacer un PATCH con la siguiente instrucción para inactivar un blog:
        http://localhost:8080/blogs/<id Blog>

## Regla 4. Solo pueden tener blogs los autores mayores de 18 años.
Hacer un POST con la siguiente instrucción para crear un blog:
    http://localhost:8080/blogs

Hacer un PUT con la siguiente instrucción para modificar un blog:
    http://localhost:8080/blogs

Los datos del blog deben ir en el body.

En ambos casos se valida que el autor ingresado sea mayor de 18 años.

## Regla 5. Solo se puede publicar un post por día.
Hacer un PATCH con la siguiente instrucción para publicar un post:
    http://localhost:8080/posts/publicar_post/<id Post>

Se valida que solo se pueda publicar un post por día para un mismo blog.

## Regla 6. Solo se puede registrar posts en blogs en estado activo.
Hacer un POST con la siguiente instrucción para crear un post:
    http://localhost:8080/blogs

Hacer un PUT con la siguiente instrucción para modificar un post:
    http://localhost:8080/blogs

Los datos del post deben ir en el body.

En ambos casos se valida que el blog ingresado se encuentre en estado activo.

El post se crea con estado borrador.

## Regla 7. Solo se pueden registrar comentarios en post en estado publicado.
Hacer un PATCH con la siguiente instrucción para registrar un comentario:
    http://localhost:8080/posts/registrar_comentario/<id Post>

Los datos del comentario deben ir en el body.

Se valida que solo se pueda registrar un comentario en un post en estado publicado.

## Regla 8. Al eliminar un autor, debe realizarse una eliminación en cadena de sus blogs, post y comentarios.
Hacer un DELETE con la siguiente instrucción para eliminar un autor:
    http://localhost:8080/authors/<id Author>

Se valida que se elimina el autor, su usuario, sus blogs y los posts de sus blogs.
Con los posts se eliminan los comentarios y reacciones.

Hacer un DELETE con la siguiente instrucción para eliminar un blog:
    http://localhost:8080/blogs/<id Blog>

Se valida que se elimina el blog y los posts del blog.
Con los posts se eliminan los comentarios y reacciones.

Hacer un DELETE con la siguiente instrucción para eliminar un post:
    http://localhost:8080/posts/<id Post>

Se valida que se elimina el post.
Con el post se eliminan los comentarios y reacciones.

## Regla 9. Un usuario solo puede tener una reacción para cada post. También puede quitarle la reacción.
Hacer un PATCH con la siguiente instrucción para registrar una reacción:
    http://localhost:8080/posts/registrar_reaccion/<id Post>

Los datos de la reacción deben ir en el body.

Se valida que un usuario solo pueda registrar una reacción en cada post.
Adicionalmente se valida que solo se pueda registrar una reacción en un post en estado publicado.

Hacer un PATCH con la siguiente instrucción para quitar una reacción:
    http://localhost:8080/posts/quitar_reaccion/<id Post>

En el body solo debe ir el Id del usuario cuya reacción se quiere quitar.

Se valida que la reacción del usuario se elimina de la lista de reacciones del post.
Adicionalmente se valida que solo se pueda quitar una reacción en un post en estado publicado.

## Regla 10.Un usuario puede comentar n veces en cada post.
Hacer un PATCH con la siguiente instrucción para registrar un comentario:
    http://localhost:8080/posts/registrar_comentario/<id Post>

Los datos del comentario deben ir en el body.

Se valida que un usuario pueda crear n comentarios.

