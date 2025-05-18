import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgChartsModule } from 'ng2-charts';
import {
  ChartType,
  ChartOptions,
  ChartData,
} from 'chart.js';

@Component({
  selector: 'app-dashboard-chart',
  standalone: true,
  imports: [CommonModule, NgChartsModule],
  templateUrl: './dashboard-chart.component.html',
  styleUrls: ['./dashboard-chart.component.scss'],
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
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'bottom',
        labels: {
          font: {
            size: 12,
            weight: 'bold',
          },
        },
      },
    },
    scales: {
      x: {
        ticks: {
          callback: function (value: string | number) {
            const label = typeof value === 'string' ? value : this.getLabelForValue(value);
            // Converte '2025-05-13' em '13/05'
            return label?.includes('-') ? label.split('-').slice(1).reverse().join('/') : label;
          },
          maxRotation: 45,
          font: {
            size: 10,
          },
        },
      },
      y: {
        ticks: {
          font: {
            size: 10,
          },
        },
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
            backgroundColor: ['#3f51b5', '#e91e63', '#ffc107', '#4caf50'],
            borderWidth: 1,
          },
        ],
      };
    }
  }
}
