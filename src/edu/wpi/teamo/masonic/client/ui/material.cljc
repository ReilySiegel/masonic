(ns edu.wpi.teamo.masonic.client.ui.material
  (:require
   #?@(:cljs [["@material-ui/core" :as mui]
              ["@material-ui/lab/TabContext" :default TabContext]
              ["@material-ui/lab/TabList" :default TabList]
              ["@material-ui/lab/TabPanel" :default TabPanel]
              ["@material-ui/lab/DatePicker" :default DatePicker]
              ["@material-ui/lab/TimePicker" :default TimePicker]
              ["@material-ui/lab/DateTimePicker" :default DateTimePicker]
              ["@material-ui/core/styles" :as styles]
              ["@material-ui/icons/Add" :default AddIcon]
              ["@material-ui/icons/Dashboard" :default DashboardIcon]
              ["@material-ui/icons/Delete" :default DeleteIcon]
              ["@material-ui/icons/ExpandMore" :default ExpandMoreIcon]
              ["@material-ui/icons/Menu" :default MenuIcon]
              ["@material-ui/icons/People" :default PeopleIcon]
              ["@material-ui/icons/AssignmentTurnedIn"
               :default AssignmentTurnedInIcon]
              ["@material-ui/icons/AssignmentInd" :default AssignmentIndIcon]
              ["@material-ui/icons/AssignmentLate" :default AssignmentLateIcon]
              ["@material-ui/icons/AssignmentReturn" :default AssignmentReturnIcon]
              ["@material-ui/icons/AssignmentReturned" :default AssignmentReturnedIcon]
              ["@material-ui/icons/LocalPharmacy" :default LocalPharmacyIcon]
              ["@material-ui/icons/LocalLaundryService" :default LocalLaundryServiceIcon]
              ["@material-ui/icons/CleanHands" :default CleanHandsIcon]
              ["@material-ui/icons/Fastfood" :default FastfoodIcon]
              ["@material-ui/icons/Redeem" :default RedeemIcon]
              ["@material-ui/lab/AdapterDateFns" :default AdapterDateFns]
              ["@material-ui/lab/LocalizationProvider" :default LocalizationProvider]])
   [com.fulcrologic.fulcro.algorithms.react-interop :as interop])
  (:refer-clojure :exclude [list]))

(def adapter-date-fns #?(:cljs AdapterDateFns :clj nil))
(def alert (interop/react-factory #?(:cljs mui/Alert :clj nil)))
(def app-bar (interop/react-factory #?(:cljs mui/AppBar :clj nil)))
(def auto-complete (interop/react-factory #?(:cljs mui/Autocomplete :clj nil)))
(def box (interop/react-factory #?(:cljs mui/Box :clj nil)))
(def button (interop/react-factory #?(:cljs mui/Button :clj nil)))
(def card (interop/react-factory #?(:cljs mui/Card :clj nil)))
(def card-action-area (interop/react-factory #?(:cljs mui/CardActionArea :clj nil)))
(def card-actions (interop/react-factory #?(:cljs mui/CardActions :clj nil)))
(def card-content (interop/react-factory #?(:cljs mui/CardContent :clj nil)))
(def card-media (interop/react-factory #?(:cljs mui/CardMedia :clj nil)))
(def checkbox (interop/react-factory #?(:cljs mui/Checkbox :clj nil)))
(def collapse (interop/react-factory #?(:cljs mui/Collapse :clj nil)))
(def container (interop/react-factory #?(:cljs mui/Container :clj nil)))
(def css-baseline (interop/react-factory #?(:cljs mui/CssBaseline :clj nil)))
(def date-picker (interop/react-factory #?(:cljs DatePicker :clj nil)))
(def date-time-picker (interop/react-factory #?(:cljs DateTimePicker :clj nil)))
(def time-picker (interop/react-factory #?(:cljs TimePicker :clj nil)))
(def dialog (interop/react-factory #?(:cljs mui/Dialog :clj nil)))
(def dialog-actions (interop/react-factory #?(:cljs mui/DialogActions :clj nil)))
(def dialog-content (interop/react-factory #?(:cljs mui/DialogContent :clj nil)))
(def drawer (interop/react-factory #?(:cljs mui/Drawer :clj nil)))
(def fab (interop/react-factory #?(:cljs mui/Fab :clj nil)))
(def form-control-label (interop/react-factory #?(:cljs mui/FormControlLabel :clj nil)))
(def grid (interop/react-factory #?(:cljs mui/Grid :clj nil)))
(def icon-button (interop/react-factory #?(:cljs mui/IconButton :clj nil)))
(def linear-progress (interop/react-factory #?(:cljs mui/LinearProgress :clj nil)))
(def list (interop/react-factory #?(:cljs mui/List :clj nil)))
(def list-item (interop/react-factory #?(:cljs mui/ListItem :clj nil)))
(def list-item-icon (interop/react-factory #?(:cljs mui/ListItemIcon :clj nil)))
(def list-item-text (interop/react-factory #?(:cljs mui/ListItemText :clj nil)))
(def localization-provider (interop/react-factory #?(:cljs LocalizationProvider :clj nil)))
(def snackbar (interop/react-factory #?(:cljs mui/Snackbar :clj nil)))
(def tab (interop/react-factory #?(:cljs mui/Tab :clj nil)))
(def tabs (interop/react-factory #?(:cljs mui/Tabs :clj nil)))
(def tab-context (interop/react-factory #?(:cljs TabContext :clj nil)))
(def tab-list (interop/react-factory #?(:cljs TabList :clj nil)))
(def tab-panel (interop/react-factory #?(:cljs TabPanel :clj nil)))
(def text-field (interop/react-input-factory #?(:cljs mui/TextField :clj nil)))
(def toolbar (interop/react-factory #?(:cljs mui/Toolbar :clj nil)))
(def tooltip (interop/react-factory #?(:cljs mui/Tooltip :clj nil)))
(def typography (interop/react-factory #?(:cljs mui/Typography :clj nil)))

(def add-icon (interop/react-factory #?(:cljs AddIcon :clj nil)))
(def dashboard-icon (interop/react-factory #?(:cljs DashboardIcon :clj nil)))
(def delete-icon (interop/react-factory #?(:cljs DeleteIcon :clj nil)))
(def expand-more-icon (interop/react-factory #?(:cljs ExpandMoreIcon :clj nil)))
(def menu-icon (interop/react-factory #?(:cljs MenuIcon :clj nil)))
(def people-icon (interop/react-factory #?(:cljs PeopleIcon :clj nil)))
(def assignment-turned-in-icon (interop/react-factory #?(:cljs AssignmentTurnedInIcon :clj nil)))
(def assignment-ind-icon (interop/react-factory #?(:cljs AssignmentIndIcon :clj nil)))
(def assignment-late-icon (interop/react-factory #?(:cljs AssignmentLateIcon :clj nil)))
(def assignment-return-icon (interop/react-factory #?(:cljs AssignmentReturnIcon :clj nil)))
(def assignment-returned-icon (interop/react-factory #?(:cljs AssignmentReturnedIcon :clj nil)))
(def local-pharmacy-icon (interop/react-factory #?(:cljs LocalPharmacyIcon :clj nil)))
(def clean-hands-icon (interop/react-factory #?(:cljs CleanHandsIcon :clj nil)))
(def local-laundry-service-icon (interop/react-factory #?(:cljs LocalLaundryServiceIcon :clj nil)))
(def fast-food-icon (interop/react-factory #?(:cljs FastfoodIcon :clj nil)))
(def redeem-icon (interop/react-factory #?(:cljs RedeemIcon :clj nil)))


(def styles-provider (interop/react-factory #?(:cljs mui/StylesProvider :clj nil)))
(def theme-provider (interop/react-factory  #?(:cljs styles/ThemeProvider :clj nil)))
(def create-mui-theme #?(:cljs (comp styles/createMuiTheme clj->js) :clj nil))
(def fade #?(:cljs styles/fade :clj nil))

(defn make-styles [f]
  #?(:cljs (comp
            #(js->clj % :keywordize-keys true)
            (mui/makeStyles (fn [theme]
                              (clj->js (f (js->clj theme :keywordize-keys true))))))))

(defn merge* [props opts]
  #?(:cljs (.assign js/Object props (clj->js opts))))

(defn ->clj [o]
  #?(:cljs (js->clj o :keywordize-keys true)))

(def use-media-query #?(:cljs mui/useMediaQuery :clj nil))

(defn use-theme []
  #?(:cljs (js->clj (styles/useTheme) :keywordize-keys true)))

(defn use-breakpoint
  ([key direction]
   (let [{{:keys [up down]} :breakpoints} (use-theme)]
     (use-media-query ((direction {:up   up
                                   :down down})
                       (name key)))))
  ([]
   (let [{{:keys [keys up]} :breakpoints} (use-theme)
         res                              (->> keys
                                                (map (comp use-media-query up))
                                                reverse
                                                (drop-while false?))]
     (or (some->> keys
                  reverse
                  (drop (- (count keys) (count res)))
                  first
                  keyword)
         :xs))))
