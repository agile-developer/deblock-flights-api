# Deblock Flights API

### Summary
This is simple service that provides a single endpoint to search for flights. The service is stateless and relies on external suppliers to provide that actual flight data. Currently, two suppliers are configured, called 'CrazyAir' and 'ToughJet'. Both these upstream services also expose a single endpoint to search for flights.

When the Deblock API is called, it calls both upstream suppliers using an HTTP client, adapts their responses to an internal model, aggregates the results and returns them to the caller.

### Running the application
This project is implemented in Kotlin (`1.9.24`) and compiled for Java 21. Please ensure the target machine has Java 21 installed, and `java` is available on the `PATH`.

Once you've cloned the repository, or unzipped the source directory, change to the `deblock-flights-api` folder and run the following command, to build sources:
```shell
./gradlew clean build
```
To start the application, run:
```shell
java -jar build/libs/deblock-flights-api-0.0.1-SNAPSHOT.jar
```
This should start the application listening on port `8080` of your local machine. To test the endpoint use the following `curl` command:
```shell
curl -v \
-H 'Content-Type: application/json' \
-d '{"origin":"LHR", "destination":"DXB", "departureDate":"2024-07-01", "returnDate":"2024-07-10", "numberOfPassengers":3}' \
http://localhost:8080/deblock/flights
```
This should return a response like this:
```json
[
  {
    "airline": "Turkish Airlines",
    "supplier": "CrazyAir",
    "fare": 450.00,
    "departureAirportCode": "LHR",
    "destinationAirportCode": "LHR",
    "departureDate": "2024-07-01T08:00:00",
    "arrivalDate": "2024-07-10T08:00:00"
  },
  {
    "airline": "Lufthansa",
    "supplier": "ToughJet",
    "fare": 495.00,
    "departureAirportCode": "LHR",
    "destinationAirportCode": "DXB",
    "departureDate": "2024-07-01T08:00:00",
    "arrivalDate": "2024-07-10T08:00:00"
  },
  {
    "airline": "Emirates",
    "supplier": "CrazyAir",
    "fare": 550.00,
    "departureAirportCode": "LHR",
    "destinationAirportCode": "LHR",
    "departureDate": "2024-07-01T08:00:00",
    "arrivalDate": "2024-07-10T08:00:00"
  },
  {
    "airline": "British Airways",
    "supplier": "CrazyAir",
    "fare": 650.00,
    "departureAirportCode": "LHR",
    "destinationAirportCode": "LHR",
    "departureDate": "2024-07-01T08:00:00",
    "arrivalDate": "2024-07-10T08:00:00"
  },
  {
    "airline": "Turkish Airlines",
    "supplier": "ToughJet",
    "fare": 715.00,
    "departureAirportCode": "LHR",
    "destinationAirportCode": "DXB",
    "departureDate": "2024-07-01T08:00:00",
    "arrivalDate": "2024-07-10T08:00:00"
  },
  {
    "airline": "Lufthansa",
    "supplier": "CrazyAir",
    "fare": 750.00,
    "departureAirportCode": "LHR",
    "destinationAirportCode": "LHR",
    "departureDate": "2024-07-01T08:00:00",
    "arrivalDate": "2024-07-10T08:00:00"
  },
  {
    "airline": "British Airways",
    "supplier": "ToughJet",
    "fare": 825.00,
    "departureAirportCode": "LHR",
    "destinationAirportCode": "DXB",
    "departureDate": "2024-07-01T08:00:00",
    "arrivalDate": "2024-07-10T08:00:00"
  },
  {
    "airline": "Emirates",
    "supplier": "ToughJet",
    "fare": 935.00,
    "departureAirportCode": "LHR",
    "destinationAirportCode": "DXB",
    "departureDate": "2024-07-01T08:00:00",
    "arrivalDate": "2024-07-10T08:00:00"
  }
]
```
#### Important
The current configuration for this service relies on `CrazyAir` and `ToughJet` to be running and accessible on the same machine. The flight 'supplier' services are configured in `/src/main/resources/application.yaml`. At least one of them should be running to see any results. Please refer to the `README` files of those services to see how to run them.
