## Antaeus

Antaeus (/ænˈtiːəs/), in Greek mythology, a giant of Libya, the son of the sea god Poseidon and the Earth goddess Gaia. He compelled all strangers who were passing through the country to wrestle with him. Whenever Antaeus touched the Earth (his mother), his strength was renewed, so that even if thrown to the ground, he was invincible. Heracles, in combat with him, discovered the source of his strength and, lifting him up from Earth, crushed him to death.

Welcome to our challenge.

## The challenge

As most "Software as a Service" (SaaS) companies, Pleo needs to charge a subscription fee every month. Our database contains a few invoices for the different markets in which we operate. Your task is to build the logic that will pay those invoices on the first of the month. While this may seem simple, there is space for some decisions to be taken and you will be expected to justify them.

### Structure
The code given is structured as follows. Feel free however to modify the structure to fit your needs.
```
├── pleo-antaeus-app
|
|       Packages containing the main() application. 
|       This is where all the dependencies are instantiated.
|
├── pleo-antaeus-core
|
|       This is where you will introduce most of your new code.
|       Pay attention to the PaymentProvider and BillingService class.
|
├── pleo-antaeus-data
|
|       Module interfacing with the database. Contains the models, mappings and access layer.
|
├── pleo-antaeus-models
|
|       Definition of models used throughout the application.
|
├── pleo-antaeus-rest
|
|        Entry point for REST API. This is where the routes are defined.
└──
```

## Instructions
Fork this repo with your solution. We want to see your progression through commits (don’t commit the entire solution in 1 step) and don't forget to create a README.md to explain your thought process.

Please let us know how long the challenge takes you. We're not looking for how speedy or lengthy you are. It's just really to give us a clearer idea of what you've produced in the time you decided to take. Feel free to go as big or as small as you want.

Happy hacking 😁!

## How to run
```
./docker-start.sh
```

## Libraries currently in use
* [Exposed](https://github.com/JetBrains/Exposed) - DSL for type-safe SQL
* [Javalin](https://javalin.io/) - Simple web framework (for REST)
* [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) - Simple logging framework for Kotlin
* [JUnit 5](https://junit.org/junit5/) - Testing framework
* [Mockk](https://mockk.io/) - Mocking library


## Brinstorming
I am new to Kotlin, so i am just trying to get the hang of what the code is and what the expecation is. My inital thoughts are to build an endpoint that can be called to pay off the invoices for customers. Some thoughts around this are to identify different timezones (hence difference in first of month to pay invoices) and different currencies.

For now, i will be focussing on writing my first few lines in Kotlin. 

Step 1: Run the app successfully :)
Step 2: Fetch all PENDING invoices - created a Rest endpoint
Step 3: Create an enpoint to pay an invoice - scope at this point is to just update status of whatever id is passed
Step 4: Set up the billing service: take dal and invoice service as members + add method to pay invoices
        New endpoint `rest/v1/invoices/update/:status` to pay invoices (patch method to update existing invoices)
          -> Calls the payinvoices functionality of billing service
            -> If first day of month (considering current timezone only for now)
              * Call paymentprovider (third party) to charge a customer for an invoice. Returns
                 - true, if charged
                    -> update the invoices to `PAID`
                 - false, if not charged because of insufficient balance? (is this a possibility since i am assuming credit cards are used?), network error,       customer not found, currency mismatch (possibility of doing currency conversion?)
                    -> update the invoices to `FAILED`
              * Returns the updated invoice list [added a new status to InvoiceStatus to track `FAILED` invioces]
            -> else, return nothing
Step 5: Consider timezones for contries for each of the currencies that is in Currency.kt 
        -> Calculate the current time in each of the timezones
        -> if any of the countries has the date as 1st of the month then return true, else false
Step 6: Consider retry option for `FAILED` invoices (to-do : need to further narrow it down to network errors only)