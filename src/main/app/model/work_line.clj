(ns app.model.work-line
  (:require [com.wsscode.pathom.connect :as pc]
            [taoensso.timbre :as log]
            [com.fulcrologic.fulcro.algorithms.tempid :as tempid]))

(def work-lines (atom {13
                       {:work-line/id 13
                       ; :work-line/project {:project/id 2 :project/name "Hello"}
                        :work-line/hours 22 }


                       14
                       {:work-line/id 14
                       ; :work-line/project {:project/id 1 :project/name "Fluxod"}
                        :work-line/hours 8 }}))

(defn next-id []
  (inc (reduce max (-> work-lines deref keys))))

(pc/defresolver work-line-resolver [env {:work-line/keys [id]}]
  {::pc/input  #{:work-line/id}
   ::pc/output [:work-line/id :work-line/hours  {:work-line/project [:project/id]}]}
  (get @work-lines id))

;; a comment

(pc/defresolver all-work-lines-resolver [_ _]
  {::pc/output [{:work-day/all-work-lines [:work-line/id]}]}
  {:work-day/all-work-lines (->> work-lines deref vals (sort-by :work-line/id) vec)})

#_(pc/defmutation save-item [env {:item/keys [id]
                                :keys      [diff]}]
  {::pc/output [:item/id]}
                                        ;(throw (ex-info "Boo" {}))
  (println "h")
  (let [new-values (get diff [:item/id id])
        new?       (tempid/tempid? id)
        real-id    (if new? (next-id) id)
        [_ category-id] (get new-values :item/category)
        new-values (cond-> new-values
                     new? (assoc :item/id real-id)
                     category-id (assoc :item/category {:category/id category-id}))]
    (log/info "Saving " new-values " for item " id)
    (Thread/sleep 500)
    (if new?
      (swap! items assoc real-id new-values)
      (swap! items update real-id merge new-values))
    (cond-> {:item/id real-id}
      new? (assoc :tempids {id real-id}))))

(def resolvers [work-line-resolver all-work-lines-resolver])
