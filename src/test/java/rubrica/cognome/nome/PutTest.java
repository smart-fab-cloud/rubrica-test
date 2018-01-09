package rubrica.cognome.nome;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class PutTest {
    
    private final WebTarget rubrica;
    private final String cognome;
    private final String nome;
    private final String numero;
    
    public PutTest() {
        // Creazione del client e connessione al servizio
        Client cli = ClientBuilder.newClient();
        rubrica = cli.target("http://localhost:50001/rubrica");
        // Inizializzazione dati del test
        this.cognome = "Rossi";
        this.nome = "Mario";
        this.numero = "+39050565758";
    }
    
    @Before
    public void aggiuntaNumero() {
        rubrica.queryParam("cognome", cognome)
                .queryParam("nome", nome)
                .queryParam("numero", numero)
                .request()
                .post(Entity.entity("", MediaType.TEXT_PLAIN));
    }
    
    @Test
    public void testAggiornamentoNumero() throws ParseException {
        String nuovoNumero = "+393256584751";
        Response rPut = rubrica.path(cognome)
                            .path(nome)
                            .queryParam("numero", nuovoNumero)   
                            .request()
                            .put(Entity.entity("",MediaType.TEXT_PLAIN));
        
        // Verifica che la risposta sia 200 Ok
        assertEquals(Response.Status.OK.getStatusCode(),rPut.getStatus());
        
        // Reperimento del numero aggiornato
        Response rGet = rubrica.path(cognome)
                            .path(nome)
                            .request()
                            .get();
        
        // Verifica che i dati del numero siano corretti
        JSONParser p = new JSONParser();
        JSONObject num = (JSONObject) p.parse(rGet.readEntity(String.class));
        assertEquals(nuovoNumero, num.get("numero"));
    }
    
    @Test 
    public void testAggiornamentoNumeroInesistente() {
        // Tentativo di aggiornamento di un numero inesistente
        Response rGet = rubrica.path(nome+cognome).path(nome+cognome).request().get();
        
        // Verifica che la risposta sia 404 Not Found
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),rGet.getStatus());
    }
    
    @Test
    public void testAggiornamentoErrato() {
        // Tentativo di put senza specifica di numero
        // e verifica ottenimento "400 Bad Request"
        Response rPut = rubrica.path(cognome)
                            .path(nome)
                            .request()
                            .put(Entity.entity("", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),rPut.getStatus());

        // Tentativo di put con numero "vuoto"
        // e verifica ottenimento "400 Bad Request"
        rPut = rubrica.path(cognome)
                            .path(nome)
                            .queryParam("numero", "")
                            .request()
                            .put(Entity.entity("", MediaType.TEXT_PLAIN));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(),rPut.getStatus());
        
    }
    
    @After
    public void eliminazioneNumero() {
        rubrica.path(cognome).path(nome).request().delete();
    }
}
