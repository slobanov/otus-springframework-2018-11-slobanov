# l01-student-testing

This is simple quiz application.
It reads question from csv file,
asks student to name himself and to answer the questions,
and prints a short report afterwards.
Application is somehow localized and you can manipulate locale by setting `quiz.locale` property.
<br>__NB__: Only `ru_RU` and `en_US` are currently supported.<br>
Application supports [Spring Shell](https://projects.spring.io/spring-shell/) to some extend (which is active by default).
You can switch back to vanilla console version by changing spring profile to `"console"`.

How to run:
* run `$ mvn clean package`
* and then, run `$ java -Dquiz.locale=ru-RU -jar target/l01-student-testing.jar`
* or, you can use docker `$ docker run --rm -it otusspring201811slobanov/l01-student-testing`

- - - -

Sergey Lobanov
[s.y.lobanov@yandex.ru](mailto:s.y.lobanov@yandex.ru?Subject=otus-springframework-2018-11-slobanov)
