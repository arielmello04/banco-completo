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

chartData: ChartData<'bar' | 'line' | 'doughnut'> = { labels: [], datasets: [] };

chartOptions: ChartOptions = {
responsive: true,
maintainAspectRatio: false,
plugins: {
legend: {
display: true,
position: 'bottom',
labels: {
color: '#e5e7eb',
font: { size: 12, weight: 500 }
}
},
tooltip: {
titleColor: '#111827',
bodyColor: '#111827',
backgroundColor: 'rgba(255,255,255,0.9)',
        borderColor: 'rgba(0,0,0,0.1)',
        borderWidth: 1
      }
    },
    scales: {
      x: {
        grid: { color: 'rgba(255,255,255,0.08)' },
        ticks: {
          color: '#cbd5e1',
          maxRotation: 45,
          font: { size: 10 }
        }
      },
      y: {
        grid: { color: 'rgba(255,255,255,0.08)' },
        ticks: {
          color: '#cbd5e1',
          font: { size: 10 }
        }
      }
    }
  };

  ngOnChanges(_: SimpleChanges): void {
    const palette = ['#7c3aed', '#06b6d4', '#22c55e', '#f59e0b', '#ef4444'];

    const base: any = {
      data: this.data,
      label: this.chartType === 'doughnut' ? undefined : this.title,
      borderWidth: 2,
      tension: 0.35,
      pointRadius: 2
    };

    if (this.chartType === 'line') {
      base.borderColor = palette[0];
      base.backgroundColor = 'rgba(124,58,237,0.25)';
      base.fill = true;
    } else if (this.chartType === 'bar') {
      base.backgroundColor = palette[1];
    } else if (this.chartType === 'doughnut') {
      base.backgroundColor = palette;
    }

    this.chartData = {
      labels: this.labels,
      datasets: [base]
    };
  }
}
