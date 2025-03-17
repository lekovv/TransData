package models

import org.apache.spark.sql.DataFrame

case class Metric(
    name: String,
    df: DataFrame
)
