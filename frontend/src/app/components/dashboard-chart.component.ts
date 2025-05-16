// src/app/components/dashboard-chart.component.ts
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgChartsModule } from 'ng2-charts';
import { ChartOptions, ChartType, ChartDataset } from 'chart.js';
import { ChartData } from 'chart.js';

@Component({
  selector: 'app-dashboard-chart',
  standalone: true,
  imports: [CommonModule, NgChartsModule],
  templateUrl: './dashboard-chart.component.html',
})
export class DashboardChartComponent {
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
      legend: { display: false },
    },
  };

  ngOnInit(): void {
  this.chartData = {
    labels: this.labels,
    datasets: [{ data: this.data, label: this.title }]
  };
}
}
