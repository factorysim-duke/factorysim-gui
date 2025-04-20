package edu.duke.ece651.factorysim.screen.ui;

import com.kotcrab.vis.ui.widget.VisTable;
import edu.duke.ece651.factorysim.Building;
import edu.duke.ece651.factorysim.screen.SimulationScreen;
import edu.duke.ece651.factorysim.screen.listeners.PolicyChangeListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Manager for info panels.
 */
public class InfoPanelManager {

    /**
     * Show the info panel for a building.
     * @param building the building to show the info panel for
     * @param infoPanelContainer the container for the info panel
     * @param requestDialogManager the request dialog manager
     * @return the info panel
     */
    public InfoPanel showBuildingInfo(Building building, VisTable infoPanelContainer, RequestDialogManager requestDialogManager) {
        InfoPanel infoPanel = BuildingInfoPanelFactory.createInfoPanel(building);

        // Update container
        infoPanelContainer.clear();
        infoPanelContainer.add(infoPanel).expand().fill().top().left().pad(5);
        infoPanelContainer.setVisible(true);

        // Attach request button listener
        attachRequestButtonListener(infoPanel, building, requestDialogManager);

        return infoPanel;
    }

    /**
     * Attach the request button listener to the info panel.
     * @param infoPanel the info panel
     * @param building the building
     * @param requestDialogManager the request dialog manager
     */
    private void attachRequestButtonListener(InfoPanel infoPanel, Building building, RequestDialogManager requestDialogManager) {
        if (infoPanel instanceof FactoryInfoPanel) {
            ((FactoryInfoPanel) infoPanel).getNewRequestButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    requestDialogManager.showRequestDialog(building);
                }
            });
        } else if (infoPanel instanceof MineInfoPanel) {
            ((MineInfoPanel) infoPanel).getNewRequestButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    requestDialogManager.showRequestDialog(building);
                }
            });
        } else if (infoPanel instanceof StorageInfoPanel) {
            ((StorageInfoPanel) infoPanel).getNewRequestButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    requestDialogManager.showRequestDialog(building);
                }
            });
        }
    }

    /**
     * Attach the policy listeners to the info panel.
     * @param infoPanel the info panel
     * @param building the building
     * @param screen the simulation screen
     */
    public void attachPolicyListeners(InfoPanel infoPanel, Building building, SimulationScreen screen) {
        // Use a factory to create appropriate policy listeners based on building type
        PolicyChangeListener policyListener = new PolicyChangeListener(screen, building);

        if (infoPanel instanceof FactoryInfoPanel) {
            FactoryInfoPanel factoryPanel = (FactoryInfoPanel) infoPanel;
            factoryPanel.getRequestPolicyBox().addListener(
                policyListener.createRequestPolicyListener(factoryPanel.getRequestPolicyBox()));
            factoryPanel.getSourcePolicyBox().addListener(
                policyListener.createSourcePolicyListener(factoryPanel.getSourcePolicyBox()));
        } else if (infoPanel instanceof MineInfoPanel) {
            MineInfoPanel minePanel = (MineInfoPanel) infoPanel;
            minePanel.getRequestPolicyBox().addListener(
                policyListener.createRequestPolicyListener(minePanel.getRequestPolicyBox()));
            minePanel.getSourcePolicyBox().addListener(
                policyListener.createSourcePolicyListener(minePanel.getSourcePolicyBox()));
        } else if (infoPanel instanceof StorageInfoPanel) {
            StorageInfoPanel storagePanel = (StorageInfoPanel) infoPanel;
            storagePanel.getRequestPolicyBox().addListener(
                policyListener.createRequestPolicyListener(storagePanel.getRequestPolicyBox()));
            storagePanel.getSourcePolicyBox().addListener(
                policyListener.createSourcePolicyListener(storagePanel.getSourcePolicyBox()));
        }
    }
}
