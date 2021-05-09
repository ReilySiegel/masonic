(ns edu.wpi.teamo.masonic.client.ui.form
  (:require [com.fulcrologic.fulcro.algorithms.denormalize :as fdenorm]
            [com.fulcrologic.fulcro.algorithms.form-state :as fs]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.components :as comp]
            [com.fulcrologic.fulcro.mutations :as m]
            [edu.wpi.teamo.masonic.client.ui.material :as mui]
            [tick.alpha.api :as tick]))

(defn- this->field [this field]
  (get (comp/props this) field))

(defn- mark-complete! [this field]
  (fn [_]
    (comp/transact! this [(fs/mark-complete! {:field field})])))

(defn text-field [this {::keys [label field]
                        :as    opts}]
  (mui/text-field
   (merge
    {:fullWidth true}
    opts
    {:label    label
     :value    (this->field this field)
     :error    (fs/invalid-spec? (comp/props this) field)
     :onBlur   (mark-complete! this field)
     :onChange #(m/set-string! this field :event %)})))

(defn auto-complete [this {::keys [label field id-key label-fn options multiple? free-solo]
                           :as    opts
                           :or    {id-key   identity
                                   label-fn identity}}]
  (mui/auto-complete
   {:renderInput          #(mui/text-field
                            (mui/merge* % {:label  label
                                           :onBlur (mark-complete! this field)}))
    :multiple             multiple?
    :value                (if multiple?
                            (map id-key (this->field this field))
                            (this->field this field))
    :onChange             (fn [_ v]
                            (m/set-value! this
                                          field
                                          (if multiple?
                                            (mapv (partial conj [id-key])
                                                  (mui/->clj v))
                                            v)))
    :disableCloseOnSelect multiple?
    :disableClearable     true
    :autoHighlight        true
    :autoSelect           true
    :freeSolo             free-solo
    :getOptionLabel       (fn [id]
                            (if (= identity id-key)
                              id
                              (let [option (first (filter (comp #{id} id-key) options))]
                                (label-fn option))))
    :options              (or  (map id-key options) [])}))

(defn date-time [this {::keys [label field]
                       :as    opts}]
  
  (mui/date-time-picker
   {:label    label
    :renderInput
    (fn [p]
      (mui/text-field
       (mui/merge*
        p
        {:fullWidth true
         :error     (fs/invalid-spec? (comp/props this) field)
         :onBlur    (mark-complete! this field)})))
    :ampm     false
    :value    (this->field this field)
    :onChange #(m/set-value! this field (try (tick/date-time %)
                                             (catch #?(:clj Exception
                                                       :cljs js/Error) e
                                               nil)))}))

(defn checkbox [this {::keys [label field]}]
  (mui/form-control-label
   {:label   label
    :control (mui/checkbox {:checked  (this->field this field)
                            :onBlur   (mark-complete! this field)
                            :onChange #(m/set-value! this
                                                     field
                                                     (.. % -target -checked))})}))

(defn dialog-actions [this form form-props upsert]
  (let [request-id    :edu.wpi.teamo.masonic.request/id
        ident         [request-id (request-id form-props)]
        state-map     (fs/mark-complete*
                       (merge/merge-component {} form form-props)
                       ident)
        updated-props (fdenorm/db->tree
                       (comp/query form)
                       (get-in state-map ident)
                       state-map)]
    (mui/dialog-actions
     {}
     (mui/button {:disabled (not (fs/dirty? form-props))
                  :onClick  #(comp/transact! this [(fs/reset-form!
                                                    {:form-ident ident})
                                                   `(m/toggle {:field :ui/open?})])} "Cancel")
     (mui/button {:disabled (fs/invalid-spec? updated-props)
                  :onClick
                  #(comp/transact! this [(upsert (select-keys form-props (fs/get-form-fields form)))
                                         `(m/toggle {:field :ui/open?})])} "Submit"))))
