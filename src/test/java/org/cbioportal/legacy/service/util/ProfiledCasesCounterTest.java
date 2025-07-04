package org.cbioportal.legacy.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.cbioportal.legacy.model.AlterationCountByGene;
import org.cbioportal.legacy.model.GenePanel;
import org.cbioportal.legacy.model.GenePanelData;
import org.cbioportal.legacy.model.GenePanelToGene;
import org.cbioportal.legacy.service.GenePanelService;
import org.cbioportal.legacy.service.SampleListService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProfiledCasesCounterTest {

  private static final String GENE_PANEL_ID_1 = "gene_panel_id_1";
  private static final String GENE_PANEL_ID_2 = "gene_panel_id_2";
  private static final Integer ENTREZ_GENE_ID_1 = 1;
  private static final Integer ENTREZ_GENE_ID_2 = 2;
  private static final Integer ENTREZ_GENE_ID_3 = 3;
  private static final Integer ENTREZ_GENE_ID_4 = 4;
  private static final String SAMPLE_ID_1 = "sample_id_1";
  private static final String SAMPLE_ID_2 = "sample_id_2";
  private static final String SAMPLE_ID_3 = "sample_id_3";
  private static final String PATIENT_ID_1 = "patient_id_1";
  private static final String PATIENT_ID_2 = "patient_id_2";

  @InjectMocks private ProfiledCasesCounter profiledSamplesCounter;

  @Mock private SampleListService sampleListService;
  @Mock private GenePanelService genePanelService;

  @Test
  public void calculate() {

    List<GenePanelData> genePanelDataList = new ArrayList<>();
    GenePanelData genePanelData1 = new GenePanelData();
    genePanelData1.setGenePanelId(GENE_PANEL_ID_1);
    genePanelData1.setProfiled(true);
    genePanelData1.setSampleId(SAMPLE_ID_1);
    genePanelData1.setPatientId(PATIENT_ID_1);
    genePanelDataList.add(genePanelData1);
    GenePanelData genePanelData2 = new GenePanelData();
    genePanelData2.setGenePanelId(GENE_PANEL_ID_2);
    genePanelData2.setProfiled(true);
    genePanelData2.setSampleId(SAMPLE_ID_2);
    genePanelData2.setPatientId(PATIENT_ID_1);
    genePanelDataList.add(genePanelData2);
    GenePanelData genePanelData3 = new GenePanelData();
    genePanelData3.setProfiled(true);
    genePanelData3.setSampleId(SAMPLE_ID_3);
    genePanelData3.setPatientId(PATIENT_ID_2);
    genePanelDataList.add(genePanelData3);

    List<GenePanel> genePanels = new ArrayList<>();
    GenePanel genePanel1 = new GenePanel();
    genePanel1.setStableId(GENE_PANEL_ID_1);
    List<GenePanelToGene> genes1 = new ArrayList<>();
    GenePanelToGene genePanelToGene1 = new GenePanelToGene();
    genePanelToGene1.setEntrezGeneId(ENTREZ_GENE_ID_1);
    genes1.add(genePanelToGene1);
    GenePanelToGene genePanelToGene2 = new GenePanelToGene();
    genePanelToGene2.setEntrezGeneId(ENTREZ_GENE_ID_2);
    genes1.add(genePanelToGene2);
    genePanel1.setGenes(genes1);
    genePanels.add(genePanel1);
    GenePanel genePanel2 = new GenePanel();
    genePanel2.setStableId(GENE_PANEL_ID_2);
    List<GenePanelToGene> genes2 = new ArrayList<>();
    GenePanelToGene genePanelToGene3 = new GenePanelToGene();
    genePanelToGene3.setEntrezGeneId(ENTREZ_GENE_ID_1);
    genes2.add(genePanelToGene3);
    GenePanelToGene genePanelToGene4 = new GenePanelToGene();
    genePanelToGene4.setEntrezGeneId(ENTREZ_GENE_ID_4);
    genes2.add(genePanelToGene4);
    genePanel2.setGenes(genes2);
    genePanels.add(genePanel2);

    Mockito.when(
            genePanelService.fetchGenePanels(
                Arrays.asList(GENE_PANEL_ID_2, GENE_PANEL_ID_1), "DETAILED"))
        .thenReturn(genePanels);

    List<AlterationCountByGene> alterationCounts = new ArrayList<>();
    AlterationCountByGene alterationCount1 = new AlterationCountByGene();
    alterationCount1.setEntrezGeneId(ENTREZ_GENE_ID_1);
    alterationCounts.add(alterationCount1);
    AlterationCountByGene alterationCount2 = new AlterationCountByGene();
    alterationCount2.setEntrezGeneId(ENTREZ_GENE_ID_2);
    alterationCounts.add(alterationCount2);
    AlterationCountByGene alterationCount3 = new AlterationCountByGene();
    alterationCount3.setEntrezGeneId(ENTREZ_GENE_ID_3);
    alterationCounts.add(alterationCount3);

    profiledSamplesCounter.calculate(
        alterationCounts, genePanelDataList, false, profiledSamplesCounter.sampleUniqueIdentifier);

    // GENE_ID_1: in panel1(S1) + panel2(S2) + no_panel(S3) = 3 samples
    Assert.assertEquals(Integer.valueOf(3), alterationCounts.get(0).getNumberOfProfiledCases());
    // GENE_ID_2: in panel1(S1) + no_panel(S3) = 2 samples
    Assert.assertEquals(Integer.valueOf(2), alterationCounts.get(1).getNumberOfProfiledCases());
    // GENE_ID_3: off-panel gene, only counts no_panel samples = 1 sample
    Assert.assertEquals(Integer.valueOf(1), alterationCounts.get(2).getNumberOfProfiledCases());

    profiledSamplesCounter.calculate(
        alterationCounts, genePanelDataList, false, profiledSamplesCounter.patientUniqueIdentifier);

    // GENE_ID_1: covers patient1 + patient2 = 2 patients
    Assert.assertEquals(Integer.valueOf(2), alterationCounts.get(0).getNumberOfProfiledCases());
    // GENE_ID_2: covers patient1 + patient2 = 2 patients
    Assert.assertEquals(Integer.valueOf(2), alterationCounts.get(1).getNumberOfProfiledCases());
    // GENE_ID_3: off-panel gene, only patient2 = 1 patient
    Assert.assertEquals(Integer.valueOf(1), alterationCounts.get(2).getNumberOfProfiledCases());

    profiledSamplesCounter.calculate(
        alterationCounts, genePanelDataList, true, profiledSamplesCounter.patientUniqueIdentifier);

    Assert.assertEquals(4, alterationCounts.size());
    // GENE_ID_1: patient1 + patient2 = 2 patients
    Assert.assertEquals(Integer.valueOf(2), alterationCounts.get(0).getNumberOfProfiledCases());
    // GENE_ID_2: patient1 + patient2 = 2 patients
    Assert.assertEquals(Integer.valueOf(2), alterationCounts.get(1).getNumberOfProfiledCases());
    // GENE_ID_3: off-panel gene, only patient2 = 1 patient
    Assert.assertEquals(Integer.valueOf(1), alterationCounts.get(2).getNumberOfProfiledCases());
    // GENE_ID_4: added from gene panel, patient1 + patient2 = 2 patients
    Assert.assertEquals(Integer.valueOf(2), alterationCounts.get(3).getNumberOfProfiledCases());
    Assert.assertEquals(ENTREZ_GENE_ID_4, alterationCounts.get(3).getEntrezGeneId());
  }
}
