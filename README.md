# react-native-sms-sender

## SendSMS
Use this RN component to send an SMS with a DeviceEventEmitter (SMS_STATUS_LISTENER). iOS and Android are both supported.

## How to install
1. `npm install react-native-sms-sender --save`

## Using the module

Once everything is all setup, it's pretty simple:
```JavaScript
SendSMS.send(myOptionsObject);
```

### Object Properties
|Key|Type|Platforms|Required?|Description|
|-|-|-|-|-|
| `body` | String | iOS/Android | No | The text that shows by default when the SMS is initiated |
| `recipients` | Array (strings) | iOS/Android | No | Provides the phone number recipients to show by default |
## Example:

```JavaScript
import SendSMS from 'react-native-sms-sender'

//some stuff

someFunction() {
	SendSMS.send({
		body: 'The default body of the SMS!',
		recipients: ['0123456789', '9876543210'],
	});
	
	//only for android |0:send,1:sent,2:delivered,3:errorSend,4:notDelivered|
	let statusListener = DeviceEventEmitter.addListener('SMS_STATUS_LISTENER', function(status) {
		console.log(status);
	});
}
```