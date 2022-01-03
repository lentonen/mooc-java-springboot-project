# "Vanhan ajan Twitter" == LeisBook

## Web-palvelinohjelmointi Java 2021 - Projekti

Kurssin projektityön ohjeet: https://web-palvelinohjelmointi-21.mooc.fi/projekti

## Projektissa käytetty
- Java
- Spring Boot
- Thymeleaf
- Bootstrap
- HTML
- CSS

## Ohjeet käynnistykseen ja sammuttamiseen komentoriviltä
1. Lataa ja asenna [Maven](https://mkyong.com/maven/how-to-install-maven-in-windows/)
2. Avaa komentorivi ja navigoi projektin juureen (kansio jossa pom.xml sijaitsee)
3. Suorita komento  `mvn spring-boot:run`
4. Nyt sovellus on käytettävissä osoitteessa http://localhost:8080/
5. Pääset kirjautumaan sovellukseen esim. tunnuksilla "aku" (passwordaku). Voit myös luoda uuden tunnuksen. HUOM! Poista database.mv.db, jos haluat startata sovelluksen tyhjällä tietokannalla.
6. Sovellus sammuu komentorivillä painamalla `ctrl + c`


## Heroku
Sovellus pyörii myös pilvessä osoitteessa:
https://wepa-project-1.herokuapp.com/

Sovellukseen on tehty 3 käyttäjää demomielessä. Voit testata sovelluksen toimintaa kirjautumalla tunnuksilla:
- tupu (passwordtupu)
- aku (passwordaku)
- iines (passwordiines)

Voit myös tehdä uudet tunnukset ja aloittaa uudella käyttäjällä.

## Tulevat päivitykset
- [ ] Parempi käyttäjän "rekisteröinti" -sivu
- [x] Kuvien kommentointi
- [x] Tykkäykset
- [x] Parannuksia ulkoasuun
