package controller;

import dao.Artista;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import bean.ArtistaFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("artistaController")
@SessionScoped
public class ArtistaController implements Serializable {

    @EJB
    private bean.ArtistaFacade ejbFacade;
    private List<Artista> items = null;
    private Artista selected;

    public ArtistaController() {
    }

    public Artista getSelected() {
        return selected;
    }

    public void setSelected(Artista selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ArtistaFacade getFacade() {
        return ejbFacade;
    }

    public Artista prepareCreate() {
        selected = new Artista();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        boolean verific = true;
        for(Artista artista:ejbFacade.findAll()){
            if(artista.getName().equals(selected.getName())){
                verific = false;
            }
            
        }if (verific){
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ArtistaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            } 
        else
        {
            JsfUtil.addErrorMessage("O nome já existe");
        }
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ArtistaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ArtistaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Artista> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Artista getArtista(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Artista> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Artista> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Artista.class)
    public static class ArtistaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ArtistaController controller = (ArtistaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "artistaController");
            return controller.getArtista(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Artista) {
                Artista o = (Artista) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Artista.class.getName()});
                return null;
            }
        }

    }

}
