# CSBot

-   Author: [Pezamosca Ștefănel](https://github.com/StefanPEZA)

## Cerinte principale

DiscordBot
-  Servicii REST, Discord API<br>
-  Crearea unui bot pentru Discord capabil sa ofere mesaje preluate prin RSS
    pe anumite teme (programare, Java, etc) si sa raspunda la intrebari simple
-  Pot fi folosite biblioteci RSS (Rich Site Summary or Really Simple
    Syndication) cum ar fi ROME

## Scurta descriere

CSBot este un bot de discord cu ajutorul caruia puteti sa aflati raspunsuri la aproape toate intrebarile voastre. Puteți căuta întrebări pe stackoverflow, sau sa puneti întrebari simple API-ului de la WolframAlpha, care va oferi raspunsuri detaliate.

Puteți să puneți intrebari de la cât face 2+2, până la care e distanța de la pământ până la soare, sau chiar să cerețti informații despre filme și alte subiecte.

Botul foloseste cate un thread pentru fiecare comanda rulata. Iar botul in sine ruleaza pe un thread separat de cel main. Atfel scriind comanda ` stop ` in consola va opri executia botului.

## Tehnologii folosite

-   #### [Discord4J](https://discord4j.com/) - [(git)](https://github.com/Discord4J/Discord4J)

Discord4J este o bibliotecă rapidă, puternică, reactivă pentru a permite dezvoltarea rapidă și ușoară a roboților Discord pentru Java, Kotlin și alte limbi JVM folosind API-ul oficial Discord Bot.

```
<dependency>
    <groupId>com.discord4j</groupId>
    <artifactId>discord4j-core</artifactId>
    <version>3.1.5</version>
</dependency>
```

-   #### [Spring Boot](https://spring.io/)

```
<!-- maven pom.xml dependency -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>5.3.7</version>
</dependency>
```

Am initializat RestTemplate-ul cu un ClientHttpRequestFactory, deoarece, in acest fel raspunsurile primite de la API-uri, in cazul in care sunt comprimate cu 'gzip', acestea vor fi decomprimate automat, fara mare efort.

```
HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
        new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());

RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
responseEntity =restTemplate.exchange(uri, HttpMethod.GET, request, Object.class);
response = (Map<String, Object>) responseEntity.getBody();
```

Am folosit clasa RestTemplate pentru a consuma servicii REST/API-uri, precum:

1. [stackexchange - search API](https://api.stackexchange.com/docs/search): folosit pentru a cauta primele 3 cele mai relevante intrebari puse pe stackoverflow pe o anumita tema sau(si) un anumit topic (tag).

```
Comanda:    #stack [intrebarea] #[tag-ul]
```

2. [Wolfram|Alpha Full Results API](https://products.wolframalpha.com/api/documentation/): folosit pentru a cere raspunsuri la majoritatea intrebarilor simple. (similare interogărilor care se pot introduce pe site-ul web Wolfram|Alpha).

```
Comanda:    #ask <intrebarea>
```

-   #### [ROME](https://rometools.github.io/rome/)

```
<!-- https://mvnrepository.com/artifact/rome/rome -->
<dependency>
    <groupId>rome</groupId>
    <artifactId>rome</artifactId>
    <version>1.0</version>
</dependency>
```

```
URL feedSource = uri.toURL();
SyndFeedInput input = new SyndFeedInput();
SyndFeed feed = input.build(new XmlReader(feedSource));
List<SyndEntryImpl> entries = feed.getEntries();
```

ROME este un framework Java pentru feed-uri RSS și atom. L-am folosit pentru a parsa feed-uri Rss/Atom, de la o varietate de surse pe anumite topicuri, precum:

```
economic       http://stiri.tvr.ro/rss/economie.xml
sport          http://stiri.tvr.ro/rss/sport.xml
extern         http://stiri.tvr.ro/rss/extern.xml
social         http://stiri.tvr.ro/rss/societate.xml
politic        http://stiri.tvr.ro/rss/politic.xml
cultura        http://stiri.tvr.ro/rss/cultura.xml
vacanta        http://stiri.tvr.ro/rss/vacanta.xml
it             https://www.techzone.ro/rss-stiri-it
reviews        https://www.techzone.ro/rss-review
software       https://www.techzone.ro/rss-stiri-software
hardware       https://www.techzone.ro/rss-stiri-hardware
jocuri         https://www.techzone.ro/rss-stiri-jocuri
lansari        https://www.techzone.ro/rss-stiri-comunicatii
monitoare      https://www.techzone.ro/rss-stiri-imagistica
evenimente_it  https://www.techzone.ro/rss-stiri-evenimente
java           https://www.infoworld.com/category/java/index.rss
programming    https://sitepoint.com/feed
tech           https://www.techmeme.com/feed.xml?x=1
all            http://stiri.tvr.ro/rss/homepage.xml
```

```
Comanda:    #news [limita] #[topic]
(daca nu preciza-ți topicul se va considera ca fiind implicit topicul `all`)
```

```
Comanda:    #stack_news [limita] #[tag-uri]
(v-a return cele mai recente intrebari puse pe stackoverflow)

Feed cu tag:    https://stackoverflow.com/feeds/tag?tagnames=${tag-uri}&sort=newest
Feed fara tag:  https://stackoverflow.com/feeds/?sort=newest
```

-   #### [Jsoup](https://jsoup.org/)

Jsoup este o librarie cu ajutorul careia se pot parsa documente HTML. L-am folosit pentru a prelua doar textul din raspunsurile unor feed-uri rss, care aveau tag-uri si entitati HTML.

```
<!-- https://mvnrepository.com/artifact/org.jsoup/jsoup -->
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.13.1</version>
</dependency>
```

```
Jsoup.parse(html).wholeText();
```

-   #### Alte comenzi

```
Comanda: #prefix [prefix nou] - arata sau seteaza prefixul comenzilor recunoscute de CSBot

Comanda: #help                - arata o lista a tuturor comenzilor disponibile

Comanda: #clear <cantitate>   - sterge o anumita cantitate de mesaje din trecut
```

Dacă vreți să rulați botul pe calculatorul vostru puteți face asta, descărcând CSBot.jar din releases, si ruland comanda ` java -jar CSBot.jar <token-ul generat de discord pentru voi> `

<br><br>
Link de invitație dacă vreți să invitați botul pe serverul vostru de discord: [invite](https://discord.com/api/oauth2/authorize?client_id=848884594450366484&permissions=8&scope=bot)
