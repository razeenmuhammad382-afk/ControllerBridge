# Controller Bridge — Android v0.1

This is the first prototype for the controller-bridge project.

## What it does

- Uses Android's built-in input system.
- Detects a connected game controller.
- Shows the controller name, device ID and input sources.
- Displays button press/release events.
- Displays left/right analog-stick values.
- Does not require a third-party controller app.

## Test

1. Open the project in Android Studio.
2. Build and install it on the Android phone.
3. Pair the controller with Android in normal Bluetooth settings.
4. Open Controller Bridge.
5. Press every button and move both sticks.
6. Check that the app reacts.

## Next stage

After this input test works, add a USB transport layer and a Windows companion program. The Windows side can then expose the received input as a virtual controller.

Note: the exact USB transport depends on how the phone exposes USB and whether the PC connection is available to the Android app. We will choose the transport after confirming the controller events.
