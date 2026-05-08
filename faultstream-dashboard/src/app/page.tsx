"use client";
import React, { startTransition, useEffect, useState } from "react";
import { Activity, AlertTriangle } from "lucide-react";
import {
  PieChart,
  Pie,
  Cell,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip as RechartsTooltip,
  ResponsiveContainer,
} from "recharts";

type DashboardStats = {
  activeNodes: number;
  recordedAnomalies: number;
  systemIntegrity: number;
  status: string;
};

type DashboardEvent = {
  id: string;
  time: string;
  equipment: string;
  faultCode: string;
  severity: "CRIT" | "WARN";
  color: string;
  value: number;
  unit: string;
};

type DashboardPattern = {
  name: string;
  value: number;
  color: string;
};

type DashboardFrequency = {
  name: string;
  faults: number;
};

type DashboardTerminal = {
  stats: DashboardStats;
  stream: DashboardEvent[];
  patternAnalysis: DashboardPattern[];
  frequencyDensity: DashboardFrequency[];
};

type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
};

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080/api/v1";

const EMPTY_DATA: DashboardTerminal = {
  stats: {
    activeNodes: 0,
    recordedAnomalies: 0,
    systemIntegrity: 100,
    status: "BOOTING",
  },
  stream: [],
  patternAnalysis: [],
  frequencyDensity: [
    { name: "D-6", faults: 0 },
    { name: "D-5", faults: 0 },
    { name: "D-4", faults: 0 },
    { name: "D-3", faults: 0 },
    { name: "D-2", faults: 0 },
    { name: "D-1", faults: 0 },
    { name: "NOW", faults: 0 },
  ],
};

export default function Dashboard() {
  const [terminal, setTerminal] = useState<DashboardTerminal>(EMPTY_DATA);
  const [isLoading, setIsLoading] = useState(true);
  const [linkState, setLinkState] = useState<"LIVE" | "DEGRADED">("DEGRADED");

  useEffect(() => {
    let active = true;
    let timeoutId: number | undefined;

    const scheduleNextLoad = () => {
      if (!active) {
        return;
      }
      const nextDelay = document.visibilityState === "visible" ? 5000 : 20000;
      timeoutId = window.setTimeout(loadDashboard, nextDelay);
    };

    const loadDashboard = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/dashboard/terminal`, {
          cache: "no-store",
        });
        if (!response.ok) {
          throw new Error(`Dashboard request failed with ${response.status}`);
        }

        const payload: ApiResponse<DashboardTerminal> = await response.json();
        if (!payload.success || !payload.data) {
          throw new Error("Dashboard payload was not successful");
        }
        if (!active) {
          return;
        }

        startTransition(() => {
          setTerminal(payload.data);
          setLinkState("LIVE");
          setIsLoading(false);
        });
      } catch {
        if (!active) {
          return;
        }
        startTransition(() => {
          setLinkState("DEGRADED");
          setIsLoading(false);
        });
      } finally {
        scheduleNextLoad();
      }
    };

    loadDashboard();

    return () => {
      active = false;
      if (timeoutId) {
        window.clearTimeout(timeoutId);
      }
    };
  }, []);

  const stats = terminal.stats;
  const logs = terminal.stream;
  const pieData = terminal.patternAnalysis;
  const barData = terminal.frequencyDensity;
  const statusTone =
    stats.status === "CRITICAL"
      ? "text-red-500"
      : stats.status === "AWARE"
        ? "text-yellow-400"
        : "text-emerald-400";

  return (
    <div className="min-h-screen bg-black text-zinc-300 p-8 font-mono selection:bg-zinc-800">
      <header className="flex flex-col md:flex-row md:items-end justify-between mb-16 pb-4 border-b border-zinc-900">
        <div>
          <div className="flex items-center gap-3 mb-2">
            <Activity className="text-white w-5 h-5" strokeWidth={1.5} />
            <h1 className="text-xl font-medium tracking-widest text-white uppercase">
              FaultStream
            </h1>
          </div>
          <p className="text-xs text-zinc-600 tracking-[0.2em] uppercase">
            Core Diagnostics Terminal v3.0.0
          </p>
        </div>
        <div className="mt-4 md:mt-0 flex items-center gap-2">
          <span className="relative flex h-2 w-2">
            <span
              className={`absolute inline-flex h-full w-full rounded-full opacity-75 ${
                linkState === "LIVE" ? "animate-ping bg-emerald-400" : "bg-yellow-500"
              }`}
            />
            <span
              className={`relative inline-flex rounded-full h-2 w-2 ${
                linkState === "LIVE" ? "bg-emerald-500" : "bg-yellow-500"
              }`}
            />
          </span>
          <span className="text-[10px] text-zinc-500 tracking-widest">
            SENSOR_LINK: {linkState}
          </span>
        </div>
      </header>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-8 mb-16">
        <Metric label="Active_Nodes" value={stats.activeNodes} />
        <Metric label="Sys_Integrity" value={`${stats.systemIntegrity.toFixed(1)}%`} />
        <Metric
          label="Recorded_Anomalies"
          value={stats.recordedAnomalies}
          emphasis="text-red-500"
        />
        <div className="flex flex-col border-l border-red-900/50 pl-4">
          <span className="text-[10px] text-red-500/70 uppercase tracking-widest mb-2">
            Status
          </span>
          <div className="flex items-center gap-2 mt-1">
            <AlertTriangle className={`w-5 h-5 ${statusTone}`} strokeWidth={1.5} />
            <span className={`text-sm font-bold tracking-widest ${statusTone}`}>
              {stats.status}
            </span>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-12">
        <div className="lg:col-span-8 flex flex-col">
          <div className="flex items-end justify-between mb-4 border-b border-zinc-900 pb-2">
            <h2 className="text-[10px] text-zinc-500 tracking-widest uppercase">
              Stream [sensor-readings]
            </h2>
            {isLoading ? (
              <span className="text-[10px] text-zinc-600 tracking-widest uppercase">
                Loading
              </span>
            ) : null}
          </div>
          <div className="w-full">
            <div className="grid grid-cols-5 text-[10px] text-zinc-600 tracking-widest uppercase mb-4 px-2">
              <div>Time</div>
              <div>Node</div>
              <div>Fault_Code</div>
              <div className="text-right">Value</div>
              <div className="text-right">Sev</div>
            </div>
            <div className="flex flex-col gap-1">
              {logs.length > 0 ? (
                logs.map((log) => (
                  <div
                    key={log.id}
                    className="grid grid-cols-5 items-center text-xs py-2 px-2 hover:bg-zinc-900/40 transition-colors border-l-2 border-transparent hover:border-zinc-700"
                  >
                    <div className="text-zinc-500">{log.time}</div>
                    <div className="text-zinc-300">{log.equipment}</div>
                    <div style={{ color: log.color }} className="tracking-wider">
                      {log.faultCode}
                    </div>
                    <div className="text-right text-zinc-400">
                      {log.value.toFixed(2)} {log.unit}
                    </div>
                    <div className="text-right">
                      <span
                        className={`px-2 py-0.5 text-[9px] tracking-widest border ${
                          log.severity === "CRIT"
                            ? "border-red-500/30 text-red-500"
                            : "border-yellow-500/30 text-yellow-500"
                        }`}
                      >
                        {log.severity}
                      </span>
                    </div>
                  </div>
                ))
              ) : (
                <div className="py-10 text-center text-sm text-zinc-600 border border-zinc-900">
                  No anomaly stream yet. If the simulator just started, wait a few seconds.
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="lg:col-span-4 flex flex-col gap-12">
          <div>
            <h2 className="text-[10px] text-zinc-500 tracking-widest uppercase mb-4 border-b border-zinc-900 pb-2">
              Pattern_Analysis
            </h2>
            <div className="h-[180px] w-full min-h-[180px]">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={pieData}
                    cx="50%"
                    cy="50%"
                    innerRadius={55}
                    outerRadius={70}
                    paddingAngle={2}
                    dataKey="value"
                    stroke="none"
                  >
                    {pieData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <RechartsTooltip
                    contentStyle={{
                      backgroundColor: "#000",
                      border: "1px solid #27272a",
                      borderRadius: "0",
                      fontSize: "12px",
                    }}
                    itemStyle={{ color: "#fff" }}
                  />
                </PieChart>
              </ResponsiveContainer>
            </div>
            <div className="grid grid-cols-2 gap-y-3 gap-x-2 mt-4 px-4">
              {pieData.map((datum) => (
                <div key={datum.name} className="flex items-center gap-2 text-[10px] text-zinc-500">
                  <div
                    className="w-1.5 h-1.5 rounded-full"
                    style={{ backgroundColor: datum.color }}
                  />
                  {datum.name}
                </div>
              ))}
            </div>
          </div>

          <div>
            <h2 className="text-[10px] text-zinc-500 tracking-widest uppercase mb-4 border-b border-zinc-900 pb-2">
              Freq_Density [7D]
            </h2>
            <div className="h-[120px] w-full min-h-[120px]">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={barData} margin={{ top: 0, right: 0, left: -30, bottom: 0 }}>
                  <XAxis
                    dataKey="name"
                    stroke="#3f3f46"
                    fontSize={9}
                    tickLine={false}
                    axisLine={false}
                  />
                  <YAxis
                    stroke="#3f3f46"
                    fontSize={9}
                    tickLine={false}
                    axisLine={false}
                  />
                  <RechartsTooltip
                    cursor={{ fill: "#18181b", opacity: 0.8 }}
                    contentStyle={{
                      backgroundColor: "#000",
                      border: "1px solid #27272a",
                      borderRadius: "0",
                      fontSize: "12px",
                    }}
                  />
                  <Bar dataKey="faults" fill="#52525b" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function Metric({
  label,
  value,
  emphasis,
}: {
  label: string;
  value: number | string;
  emphasis?: string;
}) {
  return (
    <div className="flex flex-col border-l border-zinc-800 pl-4">
      <span className="text-[10px] text-zinc-500 uppercase tracking-widest mb-2">{label}</span>
      <span className={`text-3xl tracking-tighter text-zinc-100 ${emphasis ?? ""}`}>{value}</span>
    </div>
  );
}
