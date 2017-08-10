# gemfire-durable-cq-source

SCS module implementation for Gemfire 8.x, these modules have support for durable subscriptions.

### New Properties

```
--gemfire.client.durableClientId=
--gemfire.client.durableClientTimeout=
--gemfire.durable=
```

### Example

```
stream create --name gemfirecqlog --definition "gemfire-durable-cq-source --gemfire.pool.connect-type=locator --gemfire.pool.host-addresses=10.0.0.6:55221 --gemfire.query='select * from /Customer' --gemfire.pool.subscription-enabled=true --gemfire.cq-event-expression={\"key\":key,\"value\":newValue}  --gemfire.client.durableClientId=3 --gemfire.client.durableClientTimeout=300 --gemfire.durable=true --outputType=application/json | log"
```

### Building SCS artifacts

Execute the below mvn command in the project folder to generate the prebuilt applications

```
mvn clean install -PgenerateApps
```

Once above command is executed, Navigate into <projectfolder>/apps/gemfire-cq-source-rabbit and execute following command to generate the scs modules

```
mvn clean package
```
