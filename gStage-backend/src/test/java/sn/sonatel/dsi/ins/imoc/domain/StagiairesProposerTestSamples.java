package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StagiairesProposerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static StagiairesProposer getStagiairesProposerSample1() {
        return new StagiairesProposer()
            .id(1L)
            .demandeur("demandeur1")
            .direction("direction1")
            .nbreStagiaire(1)
            .profilFormation("profilFormation1")
            .stagiaieSousRecomandation("stagiaieSousRecomandation1")
            .commentaire("commentaire1")
            .motif("motif1")
            .traitement("traitement1");
    }

    public static StagiairesProposer getStagiairesProposerSample2() {
        return new StagiairesProposer()
            .id(2L)
            .demandeur("demandeur2")
            .direction("direction2")
            .nbreStagiaire(2)
            .profilFormation("profilFormation2")
            .stagiaieSousRecomandation("stagiaieSousRecomandation2")
            .commentaire("commentaire2")
            .motif("motif2")
            .traitement("traitement2");
    }

    public static StagiairesProposer getStagiairesProposerRandomSampleGenerator() {
        return new StagiairesProposer()
            .id(longCount.incrementAndGet())
            .demandeur(UUID.randomUUID().toString())
            .direction(UUID.randomUUID().toString())
            .nbreStagiaire(intCount.incrementAndGet())
            .profilFormation(UUID.randomUUID().toString())
            .stagiaieSousRecomandation(UUID.randomUUID().toString())
            .commentaire(UUID.randomUUID().toString())
            .motif(UUID.randomUUID().toString())
            .traitement(UUID.randomUUID().toString());
    }
}
