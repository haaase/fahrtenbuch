import { defineConfig } from "vite";
import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";
import { VitePWA } from "vite-plugin-pwa";

export default defineConfig({
  plugins: [
    scalaJSPlugin(),
    VitePWA({
      registerType: "autoUpdate",
      includeAssets: [
        "favicon.ico",
        "pwa-icon.svg",
        "apple-touch-icon-180x180.png",
      ],
      manifest: {
        name: "Fahrtenbuch",
        short_name: "Fahrtenbuch",
        description: "Digitales Fahrtenbuch",
        theme_color: "#3273dc",
        background_color: "#ffffff",
        display: "standalone",
        orientation: "portrait",
        lang: "de",
        icons: [
          {
            src: "pwa-64x64.png",
            sizes: "64x64",
            type: "image/png",
          },
          {
            src: "pwa-192x192.png",
            sizes: "192x192",
            type: "image/png",
          },
          {
            src: "pwa-512x512.png",
            sizes: "512x512",
            type: "image/png",
          },
          {
            src: "maskable-icon-512x512.png",
            sizes: "512x512",
            type: "image/png",
            purpose: "maskable",
          },
        ],
      },
      workbox: {
        globPatterns: ["**/*.{js,css,html,ico,png,svg,woff,woff2}"],
        navigateFallback: "index.html",
      },
    }),
  ],
});
