package org.shanoir.uploader.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.shanoir.uploader.ShUpConfig;
import org.shanoir.uploader.dicom.IDicomServerClient;
import org.shanoir.uploader.dicom.anonymize.ISubjectIdentifierGenerator;
import org.shanoir.uploader.dicom.anonymize.Pseudonymizer;
import org.shanoir.uploader.exception.PseudonymusException;
import org.shanoir.uploader.gui.DicomTree;
import org.shanoir.uploader.gui.MainWindow;

/**
 * This class implements the logic when the download or copy button is clicked.
 * 
 * @author mkain
 * 
 */
public class DownloadOrCopyActionListener implements ActionListener {

	private static Logger logger = Logger.getLogger(DownloadOrCopyActionListener.class);

	private MainWindow mainWindow;
	private ResourceBundle resourceBundle;
	private Pseudonymizer pseudonymizer;
	private ISubjectIdentifierGenerator subjectIdentifierGenerator;
	// Introduced here to inject into DownloadOrCopyRunnable
	private IDicomServerClient dicomServerClient;

	public DownloadOrCopyActionListener(final MainWindow mainWindow, final Pseudonymizer pseudonymizer,
			final ISubjectIdentifierGenerator subjectIdentifierGenerator, final IDicomServerClient dicomServerClient) {
		this.mainWindow = mainWindow;
		this.resourceBundle = mainWindow.resourceBundle;
		this.pseudonymizer = pseudonymizer;
		this.subjectIdentifierGenerator = subjectIdentifierGenerator;
		this.dicomServerClient = dicomServerClient;
	}

	/**
	 * This method contains all the logic which is performed when the download from PACS
	 * or copy from CD/DVD button is clicked.
	 */
	public void actionPerformed(final ActionEvent event) {
		if (mainWindow.dicomTree == null) {
			return;
		}
		/**
		 * 1. Read values from GUI, entered by user
		 */
		DicomDataTransferObject dicomData = mainWindow.getSAL().getDicomData();
		dicomData = completeDicomDataWithGUIValues(dicomData);
		if (dicomData == null) {
			return;
		}
		/**
		 * 2. Generate subject identifier and hash values
		 */
		try {
			dicomData = generateSubjectIdentifierAndHashValues(dicomData);
		} catch (PseudonymusException e) {
			logger.error(e.getMessage(), e);
			JOptionPane.showMessageDialog(mainWindow.frame,
				    resourceBundle.getString("shanoir.uploader.systemErrorDialog.error.phv"),
				    resourceBundle.getString("shanoir.uploader.select.error.title"),
				    JOptionPane.ERROR_MESSAGE);
			return;
		}
		/**
		 * 3. Download from PACS or copy from CD/DVD and write upload-job.xml + nominative-data-job.xml
		 */
		final String filePathDicomDir = mainWindow.getFindDicomActionListener().getFilePathDicomDir();
		final Set<org.shanoir.dicom.importer.Serie> selectedSeries = mainWindow.getSAL().getSelectedSeries();
		Runnable runnable = new DownloadOrCopyRunnable(mainWindow.isFromPACS, dicomServerClient, filePathDicomDir, selectedSeries, dicomData);
		Thread thread = new Thread(runnable);
		thread.start();
		
		// erase information in the GUI result part
		mainWindow.dicomTree.getSelectionModel().clearSelection();
		mainWindow.dicomTree = new DicomTree(null);
		mainWindow.scrollPane.setViewportView(mainWindow.dicomTree);
		// Data reset
		mainWindow.isDicomObjectSelected = false;
		mainWindow.getSAL().setDicomData(null);
		mainWindow.getSAL().setSelectedSeries(null);
		// erase query fields
		mainWindow.patientNameTF.setText("");
		mainWindow.patientIDTF.setText("");
		mainWindow.studyDescriptionTF.setText("");
		mainWindow.model.setValue(null);
		mainWindow.studyModel.setValue(null);
		
		JOptionPane.showMessageDialog(mainWindow.frame,
			    resourceBundle.getString("shanoir.uploader.downloadOrCopy.confirmation.message"),
			    resourceBundle.getString("shanoir.uploader.downloadOrCopy.confirmation.title"),
			    JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * This method reads the data entered by the user with the GUI
	 * and puts it into the DicomDataTransferObject, when the user
	 * clicks on the download or copy button.
	 * 
	 * @param dicomData
	 * @return
	 */
	private DicomDataTransferObject completeDicomDataWithGUIValues(final DicomDataTransferObject dicomData) {
		try {
			if (mainWindow.firstNameTF.getText().isEmpty()) {
				JOptionPane.showMessageDialog(mainWindow.frame,
					    resourceBundle.getString("shanoir.uploader.import.start.firstname.empty"),
					    resourceBundle.getString("shanoir.uploader.select.error.title"),
					    JOptionPane.ERROR_MESSAGE);
				return null;
			}
			dicomData.setFirstName(mainWindow.firstNameTF.getText());
			if (mainWindow.lastNameTF.getText().isEmpty()) {
				JOptionPane.showMessageDialog(mainWindow.frame,
					    resourceBundle.getString("shanoir.uploader.import.start.lastname.empty"),
					    resourceBundle.getString("shanoir.uploader.select.error.title"),
					    JOptionPane.ERROR_MESSAGE);
				return null;
			}
			dicomData.setLastName(mainWindow.lastNameTF.getText());
			if (mainWindow.birthNameTF.getText().isEmpty()) {
				JOptionPane.showMessageDialog(mainWindow.frame,
					    resourceBundle.getString("shanoir.uploader.import.start.birthname.empty"),
					    resourceBundle.getString("shanoir.uploader.select.error.title"),
					    JOptionPane.ERROR_MESSAGE);
				return null;
			}
			dicomData.setBirthName(mainWindow.birthNameTF.getText());
			Date birthDate = ShUpConfig.formatter.parse(mainWindow.birthDateTF.getText());
			dicomData.setBirthDate(birthDate);
			if (mainWindow.msexR.isSelected())
				dicomData.setSex("M");
			if (mainWindow.fsexR.isSelected())
				dicomData.setSex("F");		
		} catch (ParseException e) {
			logger.error("Unable to convert BirthDate using formatter", e);
			return null;
		}
		return dicomData;
	}

	private DicomDataTransferObject generateSubjectIdentifierAndHashValues(DicomDataTransferObject dicomData) throws PseudonymusException {
		String subjectIdentifier = null;
		if (ShUpConfig.isModePseudonymus()) {
			dicomData = pseudonymizer.createHashValuesWithPseudonymus(dicomData);
			subjectIdentifier = subjectIdentifierGenerator.generateSubjectIdentifierWithPseudonymus(dicomData);
		} else {
			subjectIdentifier = subjectIdentifierGenerator.generateSubjectIdentifier(dicomData);
		}
		dicomData.setSubjectIdentifier(subjectIdentifier);
		return dicomData;
	}

}
