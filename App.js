import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View, TouchableOpacity, NativeModules } from 'react-native';

export default function App() {
  return (
    <View style={styles.container}>
      <Text>Open up App.js to start working on your app!</Text>

      <TouchableOpacity style = {{ paddingVertical: 10, paddingHorizontal: 30, borderRadius: 20, backgroundColor: 'black', marginTop: 20 }}
      onPress = {() => {
        NativeModules.ActivityStarter.navigateToBase()
      }}>

        <Text style = {{ color: 'white' }}>Navigate</Text>

      </TouchableOpacity>

    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
