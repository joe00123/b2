(ns b2.index
  (:use [hiccup.page :only [include-css html5 include-js]])
  (:require [clojure.java.jdbc :as jdbc]
            [ring.middleware.anti-forgery :as  anti-forgery]
            )
  )
(let [db-protocol "tcp"            ; "file|mem|tcp"
      db-host     "localhost" ; "path|host:port"
      db-name     "~/mm"]

  (def db {:classname   "org.h2.Driver" ; must be in classpath
           :subprotocol "h2"
           :subname (str db-protocol "://" db-host "/" db-name)
           ; Any additional keys are passed to the driver
           ; as driver-specific properties.
           :user     "sa"
           :password ""}))
;HTML组件
(defn comp_box 
  [item]
  [:div 
   [:div {:class "1"} "姓名哈哈"  (:name item)]
   [:div "性别" (:sex item)]])
;显示用户列表的函数
(defn showUserLst []
  (jdbc/with-db-connection [db-con db]
    (let [;; fetch some rows using this connection
          rows (jdbc/query db-con ["SELECT * FROM user " ])]
      ;; insert a copy of the first row using the same connection
      (prn rows)
      (reduce #(conj %1 (comp_box %2)) 
        [:div] 
        rows)
      ))                    
  )
(jdbc/with-db-connection [db-con db]
  (jdbc/insert! db-con :user {:name "张三1" :sex 1}))
(reduce #(conj %1 [:div (%2 :name)]) 
        [:div] 
        (list {:name "abc" :sex 1})) 
(def btitle (str ".a{" "background-color"  "}"))
(defn index []
  (html5 [:head [:style btitle ".b"]] 
         [:body 
          [:div "主很没变异 简aaa单页"]
          (showUserLst)
          ] ))
;表单字段
(defn comp-field [field]
  [:input field]
  )
;表单组件
(defn comp-form [{:keys [fields method action]}] 
  [:form {:method method :action action} 
   (vec (map   comp-field  fields))
   ]
  )
(comp-field {:name "1" :type "text"})
(comp-form {:fields 
            [{:name "1" :type "text"}
             {:name "2" :type "button"}]
            :method "GET"
            :action ""
            })
(defn td [field entity]
  (let [mField (merge {:type "text" :label "未定义"} field)]
    (print "==========" entity) 
    (list
      [:td [:label (:label mField)]] 
      [:td [:input {:name (:name field) :type (:type mField) :value ((keyword (:name field)) entity) }]] )  
    )
  )
(defn tr [col entity]
  (reduce #(conj %1 (td %2 entity)) [:tr ] col)
  )
(defn row3-row [fields entity]
  (-> (partition-all 3  fields)
      ((partial map #(tr % entity)))
      )
  )
(time (html5 (row3-row [{:name ":name"} {:name ":sex"} {:name ":sex"} {:name ":sex"}] {})))
;3列的表单
(defn col3-form [entity]
  [:form {:method "post" :action ""} 
   [:input {:type "hidden" :name "__anti-forgery-token" :value anti-forgery/*anti-forgery-token*}]
   [:table
    (row3-row (:fields (meta entity) entity) entity)
    [:tr 
     [:table
      [:td
       [:p
        [:input {:type "submit" :value "保存" :class "btn btn-primary btn-lg"} ] 
        [:input {:type "button" :value "取消" :class "btn btn-warning btn-lg"} ]
        ] 
       ]
      [:td

       ]
      ]
     
     ]
    ] 
   ]
  )
(html5 
(col3-form (with-meta {} {:fields [{:name ":name"} {:name "sex"} {:name "address"}]})))

(defn bt-form [form]
  [:div.row
   [:div {:class "col-md-12 panel-info"} 
    [:div {:class "content-box-header panel-heading"} [:div {:class "panel-title"} "案件信息"]
     [:div {:class "panel-options"} [:a {:shape "rect", :href "#", :data-rel "collapse"} [:i {:class "glyphicon glyphicon-refresh"}]] [:a {:shape "rect", :href "#", :data-rel "reload"} [:i {:class "glyphicon glyphicon-cog"}]]]
     ] 
    [:div {:class "content-box-large box-with-header"} 
     [:div.panel-body {} 
      [:div {:class "row"} 
       form
       ]]]]
   ]
  )
;标题栏
(def comp-header 
  [:div.header
           [:div.container
            [:div.row
             [:div.col-md-5
              [:div.logo
               [:h1 [:a {:href "ahdi" } "管理中心"]]]]
             [:div.col-md-5
              ]
             [:div {:class "col-md-2"} [:div {:class "navbar navbar-inverse", :role "banner"} [:nav {:class "collapse navbar-collapse bs-navbar-collapse navbar-right", :role "navigation"} [:ul {:class "nav navbar-nav"} [:li {:class "dropdown"} [:a {:shape "rect", :class "dropdown-toggle", :href "#", :data-toggle "dropdown"} "个人中心" [:b {:class "caret"}]] [:ul {:class "dropdown-menu animated fadeInUp"} [:li {} [:a {:shape "rect", :href "profile.html"} "修改密码"]] [:li {} [:a {:shape "rect", :href "login.html"} "注销"]]]]]]]]
             ]
            ]])
;左侧菜单
(def comp-left-menu
  [:div {:class "col-md-2"} 
   [:div {:class "sidebar content-box", :style "display: block;"} 
    [:ul {:class "nav"} 
     [:li {:class "current"} [:a {:shape "rect", :href "index.html"} 
                              [:i {:class "glyphicon glyphicon-home"}] " Dashboard"]]
     [:li {} [:a {:shape "rect", :href "calendar.html"} [:i {:class "glyphicon glyphicon-calendar"}] " Calendar"]]
     [:li {} [:a {:shape "rect", :href "stats.html"} 
              [:i {:class "glyphicon glyphicon-stats"}] " Statistics (Charts)"]]
     [:li {} [:a {:shape "rect", :href "tables.html"} [:i {:class "glyphicon glyphicon-list"}] " Tables"]] 
     [:li {} [:a {:shape "rect", :href "buttons.html"} [:i {:class "glyphicon glyphicon-record"}] " Buttons"]] 
     [:li {} [:a {:shape "rect", :href "editors.html"} [:i {:class "glyphicon glyphicon-pencil"}] " 案件查询"]] 
     [:li {} [:a {:shape "rect", :href "forms.html"} [:i {:class "glyphicon glyphicon-tasks"}] " 案件登记"]] 
     [:li {:class "submenu"} [:a {:shape "rect", :href "#"} [:i {:class "glyphicon glyphicon-list"}] "报表统计" [:span {:class "caret pull-right"}]] 
      [:ul {} [:li {} [:a {:shape "rect", :href "login.html"} "Login"]] 
       [:li {} [:a {:shape "rect", :href "signup.html"} "Signup"]]]]]]]
  )
(defn comp-page
  [request]
  (let [form (col3-form (with-meta (:params request) {:fields [{:name "name" :label "名称"} {:name "sex"} {:name "address"}]})) {entity :params} request]
    (html5 [:head 
            (include-css "/bootstrap/css/bootstrap.min.css" "/css/styles.css" "/css/buttons.css")
            ]
           [:body
            comp-header 
            [:div.page-content
             [:div.row
              comp-left-menu 
              [:div.col-md-10
               (bt-form form)
               ]
              ] 
             ]
            (include-js "/js/jquery.js" "/js/jquery-ui.js"  "/bootstrap/js/bootstrap.min.js" "js/custom.js")
            ]) 
    )
  )

(defn form
  []
  (html5 [:header
          (include-css "/css/buttons.css")
          ]
         [:body
          [:form
           [:input {:type "text" :name ":name" :value "上"}]
           [:input {:type "submit" :value "btn"}]
           ]
          ]
                    ))
;(use 'pl.danieljanus.tagsoup)
;(parse "file:///z/workspace/clojure/b2/resources/public/temp.html")




(seq '())
(into [] '(2 4))
