"use client";
import React, { useState, useEffect } from "react";
import { 
  Activity, AlertTriangle, Cpu, Thermometer, Zap, WifiOff 
} from "lucide-react";
import { 
  PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, Tooltip as RechartsTooltip, ResponsiveContainer
} from "recharts";
const FAULT_TYPES = [
  { type: "SYS_DESYNC", icon: Cpu, color: "#eab308" },      
  { type: "THERMAL_OVERLOAD", icon: Thermometer, color: "#ef4444" }, 
  { type: "VOLTAGE_DROP", icon: Zap, color: "#f97316" },      
  { type: "LINK_FAILURE", icon: WifiOff, color: "#52525b" }   
];
const EQUIPMENTS = ["TRB-01", "CNV-A", "PMP-B", "HUB-X", "GEN-04"];
interface FaultLog {
  id: number;
  time: string;
  eqp: string;
  type: string;
  severity: "CRIT" | "WARN";
  color: string;
}

function createRandomLog(idSeed: number): FaultLog {
  const fault = FAULT_TYPES[Math.floor(Math.random() * FAULT_TYPES.length)];
  return {
    id: Date.now() + idSeed,
    time: new Date().toLocaleTimeString('en-US', { hour12: false }),
    eqp: EQUIPMENTS[Math.floor(Math.random() * EQUIPMENTS.length)],
    type: fault.type,
    severity: fault.type === "THERMAL_OVERLOAD" ? "CRIT" : "WARN",
    color: fault.color
  };
}

export default function Dashboard() {
  const [logs, setLogs] = useState<FaultLog[]>(() => {
    const initialLogs = Array.from({ length: 9 }).map((_, i) => createRandomLog(i));
    return initialLogs.reverse();
  });
  const [stats, setStats] = useState({ active: 42, total: 23, health: 94.1 });

  useEffect(() => {
    const interval = setInterval(() => {
      setLogs((prev) => [createRandomLog(Math.random()), ...prev].slice(0, 9));
      setStats(prev => ({
        ...prev,
        total: prev.total + 1,
        health: Math.max(70, Math.min(100, prev.health + (Math.random() > 0.5 ? -0.3 : 0.1)))
      }));
    }, 3500);
    return () => clearInterval(interval);
  }, []);

  const pieData = [
    { name: "SYS_DESYNC", value: 45, color: "#eab308" },
    { name: "THERMAL", value: 20, color: "#ef4444" },
    { name: "PWR_DROP", value: 25, color: "#f97316" },
    { name: "NET_LOSS", value: 10, color: "#52525b" },
  ];
  const barData = [
    { name: "D-6", faults: 12 }, { name: "D-5", faults: 19 }, { name: "D-4", faults: 15 },
    { name: "D-3", faults: 22 }, { name: "D-2", faults: 18 }, { name: "D-1", faults: 8 }, { name: "NOW", faults: 5 },
  ];
  return (
    <div className="min-h-screen bg-black text-zinc-300 p-8 font-mono selection:bg-zinc-800">
      {}
      <header className="flex flex-col md:flex-row md:items-end justify-between mb-16 pb-4 border-b border-zinc-900">
        <div>
          <div className="flex items-center gap-3 mb-2">
            <Activity className="text-white w-5 h-5" strokeWidth={1.5} />
            <h1 className="text-xl font-medium tracking-widest text-white uppercase">FaultStream</h1>
          </div>
          <p className="text-xs text-zinc-600 tracking-[0.2em] uppercase">Core Diagnostics Terminal v2.1.0</p>
        </div>
        <div className="mt-4 md:mt-0 flex items-center gap-2">
          <span className="relative flex h-2 w-2">
            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75"></span>
            <span className="relative inline-flex rounded-full h-2 w-2 bg-red-500"></span>
          </span>
          <span className="text-[10px] text-zinc-500 tracking-widest">KAFKA_LINK: LIVE</span>
        </div>
      </header>
      {}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-8 mb-16">
        <div className="flex flex-col border-l border-zinc-800 pl-4">
          <span className="text-[10px] text-zinc-500 uppercase tracking-widest mb-2">Active_Nodes</span>
          <span className="text-3xl text-zinc-100 tracking-tighter">{stats.active}</span>
        </div>
        <div className="flex flex-col border-l border-zinc-800 pl-4">
          <span className="text-[10px] text-zinc-500 uppercase tracking-widest mb-2">Sys_Integrity</span>
          <span className="text-3xl text-zinc-100 tracking-tighter">{stats.health.toFixed(1)}%</span>
        </div>
        <div className="flex flex-col border-l border-zinc-800 pl-4">
          <span className="text-[10px] text-zinc-500 uppercase tracking-widest mb-2">Recorded_Anomalies</span>
          <span className="text-3xl text-red-500 tracking-tighter">{stats.total}</span>
        </div>
        <div className="flex flex-col border-l border-red-900/50 pl-4">
          <span className="text-[10px] text-red-500/70 uppercase tracking-widest mb-2">Status</span>
          <div className="flex items-center gap-2 mt-1">
            <AlertTriangle className="text-red-500 w-5 h-5" strokeWidth={1.5} />
            <span className="text-sm text-red-500 font-bold tracking-widest">AWARE</span>
          </div>
        </div>
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-12">
        {}
        <div className="lg:col-span-8 flex flex-col">
          <div className="flex items-end justify-between mb-4 border-b border-zinc-900 pb-2">
            <h2 className="text-[10px] text-zinc-500 tracking-widest uppercase">Stream [equipment.events]</h2>
          </div>
          <div className="w-full">
            <div className="grid grid-cols-4 text-[10px] text-zinc-600 tracking-widest uppercase mb-4 px-2">
              <div>Time</div>
              <div>Node</div>
              <div>Fault_Code</div>
              <div className="text-right">Sev</div>
            </div>
            <div className="flex flex-col gap-1">
              {logs.map((log) => (
                <div key={log.id} className="grid grid-cols-4 items-center text-xs py-2 px-2 hover:bg-zinc-900/40 transition-colors border-l-2 border-transparent hover:border-zinc-700">
                  <div className="text-zinc-500">{log.time}</div>
                  <div className="text-zinc-300">{log.eqp}</div>
                  <div style={{ color: log.color }} className="tracking-wider">{log.type}</div>
                  <div className="text-right">
                    <span className={`px-2 py-0.5 text-[9px] tracking-widest border ${
                      log.severity === 'CRIT' ? 'border-red-500/30 text-red-500' : 'border-yellow-500/30 text-yellow-500'
                    }`}>
                      {log.severity}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
        {}
        <div className="lg:col-span-4 flex flex-col gap-12">
          <div>
            <h2 className="text-[10px] text-zinc-500 tracking-widest uppercase mb-4 border-b border-zinc-900 pb-2">Pattern_Analysis</h2>
            <div className="h-[180px] w-full min-h-[180px]">
              <ResponsiveContainer width="100%" height="100%" minWidth={1} minHeight={1}>
                <PieChart>
                  <Pie data={pieData} cx="50%" cy="50%" innerRadius={55} outerRadius={70} paddingAngle={2} dataKey="value" stroke="none">
                    {pieData.map((entry, index) => (<Cell key={`cell-${index}`} fill={entry.color} />))}
                  </Pie>
                  <RechartsTooltip 
                    contentStyle={{ backgroundColor: '#000', border: '1px solid #27272a', borderRadius: '0', fontSize: '12px' }} 
                    itemStyle={{ color: '#fff' }}
                  />
                </PieChart>
              </ResponsiveContainer>
            </div>
            <div className="grid grid-cols-2 gap-y-3 gap-x-2 mt-4 px-4">
              {pieData.map(d => (
                <div key={d.name} className="flex items-center gap-2 text-[10px] text-zinc-500">
                  <div className="w-1.5 h-1.5 rounded-full" style={{ backgroundColor: d.color }}></div>
                  {d.name}
                </div>
              ))}
            </div>
          </div>
          <div>
            <h2 className="text-[10px] text-zinc-500 tracking-widest uppercase mb-4 border-b border-zinc-900 pb-2">Freq_Density [7D]</h2>
            <div className="h-[120px] w-full min-h-[120px]">
              <ResponsiveContainer width="100%" height="100%" minWidth={1} minHeight={1}>
                <BarChart data={barData} margin={{ top: 0, right: 0, left: -30, bottom: 0 }}>
                  <XAxis dataKey="name" stroke="#3f3f46" fontSize={9} tickLine={false} axisLine={false} />
                  <YAxis stroke="#3f3f46" fontSize={9} tickLine={false} axisLine={false} />
                  <RechartsTooltip 
                    cursor={{fill: '#18181b', opacity: 0.8}} 
                    contentStyle={{ backgroundColor: '#000', border: '1px solid #27272a', borderRadius: '0', fontSize: '12px' }} 
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
