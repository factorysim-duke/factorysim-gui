package edu.duke.ece651.factorysim.screen.ui;

import com.kotcrab.vis.ui.widget.VisTable;
import edu.duke.ece651.factorysim.Building;
import edu.duke.ece651.factorysim.FactoryGame;
import edu.duke.ece651.factorysim.screen.listeners.PolicyChangeListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class InfoPanelManager {

    public InfoPanel showBuildingInfo(Building building, VisTable infoPanelContainer, RequestDialogManager requestDialogManager) {
        // Create appropriate info panel for the building type
        InfoPanel infoPanel = BuildingInfoPanelFactory.createInfoPanel(building);

        // Update container
        infoPanelContainer.clear();
        infoPanelContainer.add(infoPanel).expand().fill().top().left().pad(5);
        infoPanelContainer.setVisible(true);

        // Attach request button listener
        attachRequestButtonListener(infoPanel, building, requestDialogManager);

        return infoPanel;
    }

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

    public void attachPolicyListeners(InfoPanel infoPanel, Building building, FactoryGame game) {
        // Use a factory to create appropriate policy listeners based on building type
        PolicyChangeListener policyListener = new PolicyChangeListener(game, building);

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
