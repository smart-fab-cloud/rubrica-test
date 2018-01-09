package rubrica.main;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import static org.junit.Assert.*;

public class PostTest {
    
    private final WebTarget rubrica;
    private final String cognome;
    private final String nome;
    private final String numero;
    
    public PostTest() {
        // Creazione del client e connessione al servizio
        Client cli = ClientBuilder.newClient();
        rubrica = cli.target("http://localhost:50001/rubrica");
        // Inizializzazione dati del test
        this.cognome = "Rossi";
        this.nome = "Mario";
        this.numero = "+39050565758";
    }
    
    @Test
    public void testPostSenzaNomeCreated() throws ParseException {
        // Post di un nuovo numero (senza parametro "nome")
        Response rPost = rubrica.queryParam("cognome", cognome)
                            .queryParam("numero", numero)
                            .request()
                            .post(Entity.entity("", MediaType.TEXT_PLAIN));
        
        // Reperimento della risorsa creata
        Response rGet = rubrica.path(cognome)
                            .path("famiglia")
                            .request()
                            .get();
        
        // Eliminazione della risorsa creata
        rubrica.path(cognome).path("famiglia").request().delete();
        
        // Verifica che la risposta "rPost" sia 201 Created
        assertEquals(Status.CREATED.getStatusCode(), rPost.getStatus());
        
        // Verifica che il prodotto sia stato inserito correttamente
        // (nome di default: "famiglia")
        JSONParser p = new JSONParser();
        JSONObject num = (JSONObject) p.parse(rGet.readEntity(String.class));
        assertEquals(cognome, num.get("cognome"));
        assertEquals("famiglia", num.get("nome"));
        assertEquals(numero, num.get("numero"));
    }
    
    @Test
    public void testPostConNomeCreated() throws ParseException {
        // Post di un nuovo numero
        Response rPost = rubrica.queryParam("cognome", cognome)
                            .queryParam("nome", nome)
                            .queryParam("numero", numero)
                            .request()
                            .post(Entity.entity("", MediaType.TEXT_PLAIN));
        
        // Reperimento della risorsa creata
        Response rGet = rubrica.path(cognome)
                            .path(nome)
                            .request()
                            .get();
        
        // Eliminazione della risorsa creata
        rubrica.path(cognome).path(nome).request().delete();
        
        // Verifica che la risposta "rPost" sia 201 Created
        assertEquals(Status.CREATED.getStatusCode(), rPost.getStatus());
        // Verifica che il prodotto sia stato inserito correttamente
        JSONParser p = new JSONParser();
        JSONObject num = (JSONObject) p.parse(rGet.readEntity(String.class));
        assertEquals(cognome, num.get("cognome"));
        assertEquals(nome, num.get("nome"));
        assertEquals(numero, num.get("numero"));
  
    }
    
    @Test
    public void testPostBadRequest() {
        // Tentativo di post senza specifica di numero
        // e verifica ottenimento "400 Bad Request"
        Response rPost = rubrica.queryParam("cognome", cognome)
                            .queryParam("nome", nome)
                            .request()
                            .post(Entity.entity("", MediaType.TEXT_PLAIN));
        assertEquals(Status.BAD_REQUEST.getStatusCode(),rPost.getStatus());

        // Tentativo di post con numero "vuoto"
        // e verifica ottenimento "400 Bad Request"
        rPost = rubrica.queryParam("cognome", cognome)
                            .queryParam("nome", nome)
                            .queryParam("numero", "")
                            .request()
                            .post(Entity.entity("", MediaType.TEXT_PLAIN));
        assertEquals(Status.BAD_REQUEST.getStatusCode(),rPost.getStatus());
        
        // Tentativo di post senza specifica di cognome
        // e verifica ottenimento "400 Bad Request"
        rPost = rubrica.queryParam("numero", numero)
                            .queryParam("nome", nome)
                            .request()
                            .post(Entity.entity("", MediaType.TEXT_PLAIN));
        assertEquals(Status.BAD_REQUEST.getStatusCode(),rPost.getStatus());

        // Tentativo di post con cognome "vuoto"
        // e verifica ottenimento "400 Bad Request"
        rPost = rubrica.queryParam("cognome", "")
                            .queryParam("nome", nome)
                            .queryParam("numero", numero)
                            .request()
                            .post(Entity.entity("", MediaType.TEXT_PLAIN));
        assertEquals(Status.BAD_REQUEST.getStatusCode(),rPost.getStatus());
        
    }
    
    @Test
    public void testPostConflict() {
        // Post di un nuovo numero
        Response rPost = rubrica.queryParam("cognome", cognome)
                            .queryParam("nome", nome)
                            .queryParam("numero", numero)
                            .request()
                            .post(Entity.entity("", MediaType.TEXT_PLAIN));
        
        // Post di un numero identico 
        Response rPost1 = rubrica.queryParam("cognome", cognome)
                            .queryParam("nome", nome)
                            .queryParam("numero", numero)
                            .request()
                            .post(Entity.entity("", MediaType.TEXT_PLAIN));

        // Eliminazione del numero inserito
        rubrica.path(cognome).path(nome).request().delete();
        
        // Verifica che la risposta rPost1 sia 409 Conflict
        assertEquals(Status.CONFLICT.getStatusCode(), rPost1.getStatus());
    }

}
