// src/app/components/dashboard-chart.component.ts
import { Component, Input, OnChanges, SimpleChanges, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgChartsModule } from 'ng2-charts';
import {
  ChartType,
  ChartOptions,
  ChartDataset,
  ChartData,
} from 'chart.js';

@Component({
  selector: 'app-dashboard-chart',
  standalone: true,
  imports: [CommonModule, NgChartsModule],
  templateUrl: './dashboard-chart.component.html',
})

export class DashboardChartComponent implements OnChanges {
  @Input() title = '';
  @Input() labels: string[] = [];
  @Input() data: number[] = [];
  @Input() chartType: ChartType = 'bar';

  chartData: ChartData<'bar' | 'line' | 'doughnut'> = {
    labels: [],
    datasets: [],
  };

  chartOptions: ChartOptions = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
      },
    },
  };

  ngOnChanges(changes: SimpleChanges): void {
    if (this.labels && this.data) {
      this.chartData = {
        labels: this.labels,
        datasets: [
          {
            data: this.data,
            label: this.chartType === 'doughnut' ? undefined : this.title,
          },
        ],
      };

      this.chartOptions = {
        responsive: true,
        plugins: {
          legend: {
            display: this.chartType !== 'line',
          },
        },
      };
    }
  }
}