# 🌱 Céspedes.Agro — Control App

Aplicación Android nativa para controlar dispositivos ESP32 del proyecto Céspedes.Agro vía Bluetooth Classic (SPP).

## 🎯 Funcionalidades

- ✅ Conexión BT con detección automática (Agro / Meteo)
- ✅ Control de relés R1-R6, L1, C1 (Nodo Agro)
- ✅ Control de Interruptor y Contactor (Estación Meteo)
- ✅ **🚨 Botón de pánico** con contraseña `000` para bloqueo de emergencia
- ✅ Consola de respuestas en tiempo real
- ✅ Tema oscuro oficial Céspedes.Agro
- ✅ Selector de idioma UI (ES / EN / RU)

## 🔴 Modo Emergencia

El botón rojo grande en la parte superior:

- **ON → OFF**: Envía el comando `OFF` al ESP32, que apaga TODOS los relés inmediatamente.
- **OFF → ON**: Requiere introducir la contraseña `000` para reactivar el sistema.
- Mientras está activo, todos los botones de actuadores quedan bloqueados.

## 📡 Comandos enviados

### Comunes (Agro y Meteo)
`ESTADO`, `SENSORES`, `IP`, `ESCANEAR`, `WIFI ssid|pass`, `MQTT ip`, `ID xxx`, `BORRAR`, `REINICIAR`

### Específicos Agro
`R1 1`, `R1 0`, `R2 1`, `R2 0` ... `R6 1`, `R6 0`, `L1 1`, `L1 0`, `C1 1`, `C1 0`

### Específicos Meteo
`Interruptor 1`, `Interruptor 0`, `Contactor 1`, `Contactor 0`

### Emergencia
`OFF` (activa modo pánico), `ON` (desactiva, requiere contraseña `000`)

## 🔧 Compilación

1. Clona el repositorio o descárgalo como ZIP.
2. Abre la carpeta en Android Studio (Hedgehog o superior).
3. Sincroniza Gradle (espera a que descargue las dependencias).
4. Conecta un dispositivo Android físico (el Bluetooth clásico no funciona en el emulador).
5. Pulsa el botón ▶️ Run.

## 📱 Uso

1. Empareja tu ESP32 desde los ajustes de Bluetooth de Android (una sola vez).
2. Abre la app → selecciona el dispositivo de la lista.
3. Usa los botones para enviar comandos.
4. Lee las respuestas en la consola inferior.

## 📄 Licencia

Privado — Leno Perdomo Céspedes © 2026
