/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ohtu.verkkokauppa;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 *
 * @author tainalep
 */
public class KauppaTest {

    // luodaan ensin mock-oliot
    Pankki pankki;
    Viitegeneraattori viite;
    Varasto varasto;

    // sitten testattava kauppa 
    Kauppa k;

    @Before
    public void setUp() {
        // luodaan ensin mock-oliot
        pankki = mock(Pankki.class);
        viite = mock(Viitegeneraattori.class);
        varasto = mock(Varasto.class);

        // sitten testattava kauppa 
        k = new Kauppa(varasto, pankki, viite);

    }

    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaan() {

        // määritellään että viitegeneraattori palauttaa viitten 42
        when(viite.uusi()).thenReturn(42);

        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu
        verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(), anyInt());
        // toistaiseksi ei välitetty kutsussa käytetyistä parametreista
    }

    // aloitetaan asiointi, koriin lisätään tuote, jota varastossa on ja suoritetaan ostos, 
    // eli kutsutaan metodia kaupan tilimaksu(). varmistettava että kutsutaan pankin 
    // metodia tilisiirto oikealla asiakkaalla, tilinumerolla ja summalla
    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikeillaTiedoilla() {

        // määritellään että viitegeneraattori palauttaa viitten 42
        when(viite.uusi()).thenReturn(42);

        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu oikeilla parametreilla 
        verify(pankki).tilisiirto(eq("pekka"), eq(42), eq("12345"), eq(k.getKaupanTili()), eq(5));
    }

    //aloitetaan asiointi, koriin lisätään kaksi eri tuotetta, joita varastossa on ja suoritetaan ostos. 
    // varmistettava että kutsutaan pankin metodia tilisiirto oikealla asiakkaalla, tilinumerolla ja summalla
    @Test
    public void kahdenEriTuotteetOstoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikeillaTiedoilla() {

        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        // määritellään että tuote numero 2 on suklaa jonka hinta on 3 ja saldo 20
        when(varasto.saldo(2)).thenReturn(20);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "suklaa", 3));

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(2);     // ostetaan tuotetta numero 2 eli suklaata

        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu oikeilla parametreilla 
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(8));
    }

    // aloitetaan asiointi, koriin lisätään kaksi samaa tuotetta jota on varastossa tarpeeksi 
    //ja suoritetaan ostos. varmistettava että kutsutaan pankin metodia tilisiirto oikealla asiakkaalla,
    //tilinumerolla ja summalla
    @Test
    public void kahdenSamanTuotteetOstoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikeillaTiedoilla() {

        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(1);     // ostetaan toinen sama

        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu oikeilla parametreilla 
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(10));
    }

    // aloitetaan asiointi, koriin lisätään tuote jota on varastossa tarpeeksi ja tuote joka on loppu 
    // ja suoritetaan ostos. varmistettava että kutsutaan pankin metodia tilisiirto oikealla 
    // asiakkaalla, tilinumerolla ja summalla
    @Test
    public void yhdenPuuttuvanTuotteenJaYhdenToisenTuotteenOstoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikeillaTiedoilla() {

        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        // määritellään että tuote numero 2 on suklaa jonka hinta on 3 ja saldo 0
        when(varasto.saldo(2)).thenReturn(0);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "suklaa", 3));

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(2);     // yritetään ostaa tuotetta numero 2 eli suklaata

        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu oikeilla parametreilla 
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(5));
    }

    // varmistettava, että metodin aloitaAsiointi kutsuminen nollaa edellisen ostoksen tiedot
    // (eli edellisen ostoksen hinta ei näy uuden ostoksen hinnassa)
    @Test
    public void edellinenOstosNollautuu() {

        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        // määritellään että tuote numero 2 on suklaa jonka hinta on 3 ja saldo 20
        when(varasto.saldo(2)).thenReturn(20);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "suklaa", 3));

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(2);     // yritetään ostaa tuotetta numero 2 eli suklaata

        k.tilimaksu("pekka", "12345");

        // tehdään toiset ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(2);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(2);     // yritetään ostaa tuotetta numero 2 eli suklaata

        k.tilimaksu("maija", "54321");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu oikeilla parametreilla 
        verify(pankki).tilisiirto(eq("maija"), anyInt(), eq("54321"), anyString(), eq(6));

    }

    // varmistettava, että kauppa pyytää uuden viitenumeron jokaiselle maksutapahtumalle, 
    // katso tarvittaessa apua projektin MockitoDemo testeistä!
    @Test
    public void pyydetaanUusiViiteJokaiseenMaksuun() {

        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("maija", "1111");

        // tarkistetaan että tässä vaiheessa viitegeneraattorin metodia seuraava()
        // on kutsuttu kerran
        verify(viite, times(1)).uusi();

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("liisa", "22222");

        // tarkistetaan että tässä vaiheessa viitegeneraattorin metodia seuraava()
        // on kutsuttu kaksi kertaa
        verify(viite, times(2)).uusi();

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("leena", "33333");

        // tarkistetaan että tässä vaiheessa viitegeneraattorin metodia seuraava()
        // on kutsuttu kolme kertaa        
        verify(viite, times(3)).uusi();
    }
    
    
    @Test
    public void poistoKoristaJaMuidenOstoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikeillaTiedoilla() {

        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        // määritellään että tuote numero 2 on suklaa jonka hinta on 3 ja saldo 20
        when(varasto.saldo(2)).thenReturn(20);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "suklaa", 3));

        // tehdään ostokset
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(2);     // yritetään ostaa tuotetta numero 2 eli suklaata
        k.poistaKorista(2); // poistetaan sittenkin suklaa

        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu oikeilla parametreilla 
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(5));
    }

}
