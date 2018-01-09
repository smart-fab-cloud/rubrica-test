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

public class GetTest {
    
    private final WebTarget rubrica;
    private final String cognome;
    private final String nome;
    private final String numero;
    
    public GetTest() {
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
    public void testGetOk() throws ParseException {
        Response rGet = rubrica.path(cognome)
                            .path(nome)
                            .request()
                            .get();
        
        // Verifica che la risposta sia 200 Ok
        assertEquals(Response.Status.OK.getStatusCode(),rGet.getStatus());
        
        // Verifica che i dati del numero siano corretti
        JSONParser p = new JSONParser();
        JSONObject num = (JSONObject) p.parse(rGet.readEntity(String.class));
        assertEquals(cognome, num.get("cognome"));
        assertEquals(nome, num.get("nome"));
        assertEquals(numero, num.get("numero"));
    }
    
    @Test 
    public void testGetKo() {
        // Reperimento di un numero inesistente
        Response rGet = rubrica.path(nome+cognome).path(nome+cognome).request().get();
        
        // Verifica che la risposta sia 404 Not Found
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(),rGet.getStatus());
    }
    
    @After
    public void eliminazioneNumero() {
        rubrica.path(cognome).path(nome).request().delete();
    }
}
