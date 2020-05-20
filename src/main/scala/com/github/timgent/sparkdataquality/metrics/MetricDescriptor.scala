package com.github.timgent.sparkdataquality.metrics

import com.github.timgent.sparkdataquality.metrics.MetricCalculator.{ComplianceMetricCalculator, SizeMetricCalculator}
/**
 * Describes the metric being calculated
 */
sealed trait MetricDescriptor {
  type MC <: MetricCalculator
  def metricCalculator: MC
  def toSimpleMetricDescriptor: SimpleMetricDescriptor
}

object MetricDescriptor {
  trait Filterable {
    def filter: MetricFilter
  }

  case class SizeMetricDescriptor(filter: MetricFilter = MetricFilter.noFilter) extends MetricDescriptor with Filterable {
    override def metricCalculator: SizeMetricCalculator = SizeMetricCalculator(filter)
    override def toSimpleMetricDescriptor: SimpleMetricDescriptor = SimpleMetricDescriptor("Size", Some(filter.filterDescription))
    override type MC = SizeMetricCalculator
  }

  case class ComplianceMetricDescriptor(complianceFn: ComplianceFn,
                                        filter: MetricFilter = MetricFilter.noFilter) extends MetricDescriptor with Filterable {
    override def metricCalculator: ComplianceMetricCalculator = ComplianceMetricCalculator(complianceFn, filter)
    override def toSimpleMetricDescriptor: SimpleMetricDescriptor =
      SimpleMetricDescriptor("Compliance", Some(filter.filterDescription), Some(complianceFn.description))
    override type MC = ComplianceMetricCalculator
  }
}

/**
 * Representation of a MetricDescriptor which is easy to persist
 */
private [sparkdataquality] case class SimpleMetricDescriptor(metricName: String,
                                                             filterDescription: Option[String] = None,
                                                             complianceDescription: Option[String] = None)

