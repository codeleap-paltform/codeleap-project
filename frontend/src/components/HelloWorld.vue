<script setup>
import { ref } from 'vue'
import viteLogo from '../assets/vite.svg'
import heroImg from '../assets/hero.png'
import vueLogo from '../assets/vue.svg'

const count = ref(0)
</script>

<template>
  <section id="center">
    <div class="hero">
      <img :src="heroImg" class="base" width="170" height="179" alt="" />
      <img :src="vueLogo" class="framework" alt="Vue logo" />
      <img :src="viteLogo" class="vite" alt="Vite logo" />
    </div>
    <div>
      <h1>Get started</h1>
      <p>Edit <code>src/App.vue</code> and save to test <code>HMR</code></p>
    </div>
    <button type="button" class="counter" @click="count++">
      Count is {{ count }}
    </button>
  </section>

  <div class="ticks"></div>
</template>

<style scoped>
.hero { position: relative; }
.hero .base { display: block; }
.counter {
  font-family: inherit;
  display: inline-block;
  padding: 0.5em 1.2em;
  margin-top: 1.5em;
  border: 1px solid transparent;
  border-radius: 8px;
  background: #1a1a1a;
  color: #fff;
  cursor: pointer;
  transition: border-color 0.25s;
}
.counter:hover { border-color: #646cff; }
</style>
