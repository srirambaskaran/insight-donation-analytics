# Analytics on Streaming data - Donation contributions by individuals.

This is an analytics project that provides feedback on repeated campaign contributions from a given area (or zip-code) in the United States.

## Business Aspect
The results of this analytics aims to answer the following not-so-exhaustive-yet-important business aspects.

1. Identify areas with large number repeated donors
1. Correlate with the money spent in these areas
1. Allow better utilization of resources in terms of money and man
1. Targetted campaigning

## Overview

The data about each and every donation is considered to be streaming in and very limited amount of storage is available. This considers the committee and the area to which an individual has made a donation. Without storing any individual's information, a summary of the area is calculated from time to time and emitted as output.

The program currently reads from a file but the design has been made to be to abstract a stream reader. The input and output modules can be easily replaced with a streaming service like Kafka or Storm.

More informaiton about this analytics module has been provided here [Insight Donation Analytics Challenge](https://github.com/InsightDataScience/donation-analytics/).

## Details about implementation & handling corner cases

### Calculating repeated donors

Repeated donors are any individual who has donated to a committee in the same area over years. Since the data is not streaming in chronological order, it is important to know if the order in which we see donations actually constitutes repetition.

To tackle this problem, we can just store the `earliest donation record` that a `Donor` has made that has been streamed in at any given time `t`. We consider any `Donor` who has donated after the mentioned `earliest donation record` will be a repeated donor. At any time `t + k` if we find a donation prior to `earliest donation record` we simply update this without considering the donation as repeated one.

#### Handling same year donations by same Donor

On looking at the data, we can see that a `Donor` can make more than one donation or the committee can add an amendment. We consider both cases as individual contributions which the `Donor` has done separately.

In some cases, the second transaction by a `Donor` will determine the `Donor` as repeated Donor. 
