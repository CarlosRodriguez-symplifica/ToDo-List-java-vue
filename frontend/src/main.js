import './assets/main.css'
import "vuetify/styles"
import "@mdi/font/css/materialdesignicons.css"
import "@fortawesome/fontawesome-free/css/all.css"

import { createVuetify } from "vuetify"
import * as components from "vuetify/components"
import * as directives from "vuetify/directives"

import { fa } from "vuetify/iconsets/fa"
import { aliases, mdi } from "vuetify/lib/iconsets/mdi"

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

const vuetify = createVuetify({
  theme: {
    defaultTheme: "light",
  },
  icons: {
    defaultSet: "mdi",
    aliases,
    sets: {
      mdi,
      fa,
    },
  },
  components,
  directives,
})

const app = createApp(App)

app.use(createPinia())
app.use(vuetify)
app.use(router)

app.mount('#app')
