DBDroid Remoting allows an Android application to communicate with a remote server via web services.
For this, DBDroid Remoting serializes an object (Entity) in XML and sends the XML to remote server which can process something according to the service implementation.
If the web service returns a result, it is serialized into XML and sent to the Android application which deserializes the result.